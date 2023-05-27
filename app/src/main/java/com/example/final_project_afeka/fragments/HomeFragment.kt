package com.example.final_project_afeka.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.final_project_afeka.MainViewModel
import com.example.final_project_afeka.R
import com.example.final_project_afeka.data.PermissionData
import com.example.final_project_afeka.login.LoginActivity
import com.example.final_project_afeka.ui.pages.HomePage
import com.example.final_project_afeka.utils.permissions.Permission
import com.example.final_project_afeka.utils.permissions.PermissionRequestHandlerImpl
import kotlinx.coroutines.launch

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception


class HomeFragment : Fragment(R.layout.fragment_home) {
    fun testApiKey(apiKey: String) {
        val client = OkHttpClient()
        val lat = 0.0
        val lng = 0.0
        val geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lng&key=$apiKey"

        val request = Request.Builder()
            .url(geocodingUrl)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Log.e("API_KEY_TEST", "Error testing API key.", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.d("API_KEY_TEST", "API key might be invalid.")
                        return
                    }

                    val jsonResponse = JSONObject(response.body!!.string())
                    val status = jsonResponse.getString("status")

                    if (status == "OK") {
                        Log.d("API_KEY_TEST", "API key is valid.")
                    } else {
                        Log.d("API_KEY_TEST", "API key might be invalid.")
                    }
                }
            }
        })
    }

    private val viewModel: MainViewModel by activityViewModels()
    private var openMap = false
    lateinit var permissionManager: PermissionRequestHandlerImpl

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        testApiKey("AIzaSyDqU4AcEJQPDKc_SRON8ALo_1VfmSW1nuM")
        permissionManager = viewModel.permissionManager
        permissionManager.from(this@HomeFragment)
        initPermissionResultFlow()
        viewModel.backToHome = false
        if (viewModel.initialStartTime != -1L) {
            if (foregroundPermissionApproved()) {
                viewModel.startDriving(viewModel.initialStartTime)
                findNavController().navigate(R.id.action_to_driveFragment)
            } else {
                openMap = false
                requestForegroundPermissions()
            }
        }
        view.findViewById<ComposeView>(R.id.composeViewHome).setContent {
            HomePage(
                viewModel = viewModel,
                onBack = { logout() },
                goToMap = {
                    if (foregroundPermissionApproved()) {
                        findNavController().navigate(R.id.action_to_mapFragment)
                    } else {
                        openMap = true
                        requestForegroundPermissions()
                    }
                },
                startDriving = {
                    if (foregroundPermissionApproved()) {
                        viewModel.startDriving()
                        findNavController().navigate(R.id.action_to_driveFragment)
                    } else {
                        openMap = false
                        requestForegroundPermissions()
                    }
                }
            )
        }
    }


    private fun requestForegroundPermissions() {
        lifecycleScope.launch {
            viewModel.permissionRequesterFlow.emit(
                PermissionData(
                    Permission.LocationAndNotifications, getString(R.string.permission_rationale)
                ),
            )
        }
    }


    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) && if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) true else
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS,
            )
    }


    private fun initPermissionResultFlow() {
        lifecycleScope.launch {
            viewModel.permissionRequesterFlow.collect { permissionData ->
                permissionManager
                    .request(Permission.LocationAndNotifications)
                    .rationale(permissionData.rationale)
                    .checkDetailedPermission {
                        if (foregroundPermissionApproved() && !viewModel.backToHome) {

                            if (openMap) {
                                viewModel.startService()
                                findNavController().navigate(R.id.action_to_mapFragment)
                            }
                            else {
                                viewModel.startDriving()
                                findNavController().navigate(R.id.action_to_driveFragment)
                            }
                        }
                        viewModel.backToHome = false

                    }
            }

        }
    }
    private fun logout() {
        activity?.startActivity(Intent(requireActivity(),
            LoginActivity::class.java))
        activity?.finish()
    }
}