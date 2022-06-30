package de.seemoo.at_tracking_detection.ui.dashboard

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import de.seemoo.at_tracking_detection.ATTrackingDetectionApplication
import de.seemoo.at_tracking_detection.R
import de.seemoo.at_tracking_detection.databinding.FragmentDashboardBinding
import de.seemoo.at_tracking_detection.util.Util
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()

    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dashboard,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = dashboardViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineGraphChart = view.findViewById<RallyLineGraphChart>(R.id.line_graph)
        val fabScan = view.findViewById<ExtendedFloatingActionButton>(R.id.dashboard_scan_fab)

        fabScan.setOnClickListener {
            val bluetoothManager = ATTrackingDetectionApplication.getAppContext()
                .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val hasScanPermission =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.S || Util.checkAndRequestPermission(
                    Manifest.permission.BLUETOOTH_SCAN
                )
            val isBluetoothEnabled = bluetoothManager.adapter.state == BluetoothAdapter.STATE_OFF
            if (isBluetoothEnabled && hasScanPermission) {
                AlertDialog.Builder(context).setIcon(R.drawable.ic_warning)
                    .setTitle(getString(R.string.scan_enable_bluetooth_title))
                    .setMessage(getString(R.string.scan_enable_bluetooth_message))
                    .setPositiveButton(getString(R.string.ok_button)) { _, _ ->
                        showManualScan()
                    }
                    .setNegativeButton(R.string.no_button, null).create().show()
            } else {
                showManualScan()
            }
        }

        dashboardViewModel.getBeaconHistory(dateTime.minusDays(HISTORY_LENGTH))
            .observe(viewLifecycleOwner) { beaconList ->
                Timber.d("beacon list size: ${beaconList.size}")
                val dataPoints = mutableListOf<DataPoint>()
                (0..HISTORY_LENGTH).forEach { counter ->
                    val dayOfMonth = dateTime.minusDays(counter).dayOfMonth
                    val amount =
                        beaconList.filter { it.receivedAt.dayOfMonth == dayOfMonth }
                            .distinctBy { it.deviceAddress }.size
                    dataPoints.add(counter.toInt(), DataPoint(amount.toFloat()))
                }
                lineGraphChart.addDataPoints(dataPoints.reversed())
                Timber.d("Added ${dataPoints.size} new data points to the graph chart!")
            }

        view.findViewById<MaterialCardView>(R.id.statistics_number_beacons)
            .setOnClickListener { showDeviceList() }
        view.findViewById<MaterialCardView>(R.id.statistics_number_devices)
            .setOnClickListener { showDeviceMap() }
    }

    private fun showManualScan() {
        val directions: NavDirections = DashboardRiskFragmentDirections.dashboardToScanFragment()
        findNavController().navigate(directions)
    }

    private fun showDeviceList() {
//        val directions: NavDirections = DashboardRiskFragmentDirections.dashboardToDevicesFragment(true)
//        findNavController().navigate(directions)
    }

    private fun showDeviceMap() {
//        val directions: NavDirections = DashboardRiskFragmentDirections.dashboardToDeviceMap()
//        findNavController().navigate(directions)
    }

    companion object {
        private val dateTime = LocalDateTime.now(ZoneOffset.UTC)
        private const val HISTORY_LENGTH = 14L
    }

}