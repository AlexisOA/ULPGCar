package com.alexisoa.ulpgcar.presentation.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Address
import com.alexisoa.ulpgcar.data.model.Features
import com.alexisoa.ulpgcar.data.model.Places
import com.alexisoa.ulpgcar.data.repository.location.DataPlacesImp
import com.alexisoa.ulpgcar.data.repository.location.LocationRepoImp
import com.alexisoa.ulpgcar.data.repository.routes.DateRouteRepoImp
import com.alexisoa.ulpgcar.data.repository.routes.RoutesRepoImp
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentMapsBinding
import com.alexisoa.ulpgcar.domain.interactor.routes.RoutesInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.search.SearchInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.search.adapters.DepartureAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.routes.RoutesViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.routes.RoutesViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.search.SearchViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.search.SearchViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.gson.Gson

class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapLongClickListener,  DepartureAdapter.OnLocationClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener{
    private lateinit var binding : FragmentMapsBinding
    private val viewModelSearch by viewModels<SearchViewModel> { SearchViewModelFactory(SearchInteractorImp(LocationRepoImp(DataPlacesImp()), TravelsRepositoryImp())) }
    private lateinit var map: GoogleMap
    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var adapter: DepartureAdapter
    private var placeList = ArrayList<Places>()
    var hashMapMarker: HashMap<String, Marker> = HashMap()
    private val viewModelRoutes by viewModels<RoutesViewModel> { RoutesViewModelFactory(
        RoutesInteractorImp(RoutesRepoImp(DateRouteRepoImp())))}
    var coordinatesInit = LatLng(0.0, 0.0)
    var coordinatesFin = LatLng(0.0, 0.0)
    var polyline : Polyline? = null
    private var pointsLine = ArrayList<LatLng>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapsBinding.bind(view)
        adapter = DepartureAdapter(requireContext(), placeList, this)
        createFragmentMap()
        setupRecyclerViewInit()
        setupRecyclerViewFin()
        listenerSearchLocation()
        listenerEditOrigin()
        listenerEditFin()
        listenerEndIconInit()
        listenerEditIconFin()
        gpsClickListenerInit()
        gpsClickListenerFin()
        listenerBtnContinue()
        if (prefs.getLatOrigin().isNotEmpty() && prefs.getLatDest().isNotEmpty()){
            binding.btnNextMap.setBackgroundResource(R.drawable.button_rounded)
            binding.btnNextMap.isEnabled = true
        }

        if (prefs.getEdit().equals("edit")){
            binding.btnNextMap.text = "Actualizar"
        }


    }

    private fun listenerBtnContinue() {
        binding.btnNextMap.setOnClickListener {
            println(prefs.getType())
            if (prefs.getEdit().equals("edit")){
                findNavController().previousBackStackEntry?.savedStateHandle?.set("prefs", "ok")
                findNavController().popBackStack()
            }else{
                findNavController().navigate(R.id.action_navigations_maps_to_navigation_publish_date)
            }
        }
    }

    private fun viewModelRoute() {
        savePrefsData()
        viewModelRoutes.getDataRoutes(coordinatesInit, coordinatesFin)
            .observe(viewLifecycleOwner, Observer { result: Resource<List<Features>> ->
                when(result){
                    is Resource.Loading->onLoadingRoute()
                    is Resource.Success-> onSuccessRoute(result)
                    is Resource.Failure->onFailedRoute(result)
                }
            })
    }

    private fun savePrefsData() {
        prefs.saveLatOrigin(coordinatesInit.latitude.toString())
        prefs.saveLonOrigin(coordinatesInit.longitude.toString())
        prefs.saveNameOrigin(binding.editSearchInit.text.toString())
        prefs.saveLatDest(coordinatesFin.latitude.toString())
        prefs.saveLonDest(coordinatesFin.longitude.toString())
        prefs.saveNameDest(binding.editSearchFin.text.toString())
    }

    private fun onLoadingRoute() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onFailedRoute(result: Resource.Failure<List<Features>>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
        println(result.exception.message.toString())
    }
    private fun onSuccessRoute(result: Resource.Success<List<Features>>) {
        binding.progressBar.visibility = View.GONE
        drawRouteInMap(result.data)
    }

    private fun drawRouteInMap(coordinates: List<Features>) {
        if (polyline!= null){
            polyline!!.remove()
            pointsLine.clear()

        }
        val polylineOptions = PolylineOptions()
        for (arr in coordinates[0].routes.coordinates) {
            polylineOptions.add(LatLng(arr[1] , arr[0]))
            pointsLine.add(LatLng(arr[1] , arr[0]))
        }
        val gson = Gson()
        val json: String = gson.toJson(pointsLine)
        prefs.saveJsonRoute(json)
        polyline = map.addPolyline(polylineOptions)
    }


    private fun listenerEditIconFin() {
        binding.inputEditFin.setEndIconOnClickListener(View.OnClickListener {
            binding.editSearchFin.setText("")
            if (hashMapMarker.get("Dest") != null){
                val marker = hashMapMarker.get("Dest")
                marker!!.remove()
                hashMapMarker.remove("Dest")
            }
            if (polyline!= null){
                polyline!!.remove()
            }
            statusBtnNext()
        })
    }

    private fun listenerEndIconInit() {
        binding.inputEditInit.setEndIconOnClickListener(View.OnClickListener {
            binding.editSearchInit.setText("")
            if (hashMapMarker.get("Init") != null){
                val marker = hashMapMarker.get("Init")
                marker!!.remove()
                hashMapMarker.remove("Init")
            }
            if (polyline!= null){
                polyline!!.remove()
            }
            statusBtnNext()
        })
    }


    private fun listenerEditFin() {
        binding.editSearchFin.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                if (!binding.editSearchFin.text.isNullOrEmpty()){
                    binding.searchListFin.visibility = View.VISIBLE
                    viewModelSearch.setLocation(binding.editSearchFin.text.toString())
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupRecyclerViewFin() {
        with(binding){
            searchListFin.layoutManager = LinearLayoutManager(requireContext())
            searchListFin.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun listenerEditOrigin() {
        binding.editSearchInit.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                if (!binding.editSearchInit.text.isNullOrEmpty()){
                    binding.searchListInit.visibility = View.VISIBLE
                    binding.editSearchFin.inputType = InputType.TYPE_NULL
                    viewModelSearch.setLocation(binding.editSearchInit.text.toString())
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupRecyclerViewInit() {
        with(binding){
            searchListInit.layoutManager = LinearLayoutManager(requireContext())
            searchListInit.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun createFragmentMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        val locationButton= (mapFragment.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))

        val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
        rlp.setMargins(0,0,30,30)
    }

    private fun listenerSearchLocation() {
        viewModelSearch.searchLocation()
                .observe(viewLifecycleOwner, Observer{ result: Resource<ArrayList<Places>> ->
                    when(result){
                        is Resource.Loading->onLoading()
                        is Resource.Success-> onSuccess(result)
                        is Resource.Failure->onFailed(result)
                    }
                })
    }

    private fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onFailed(result: Resource.Failure<ArrayList<Places>>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
        println(result.exception.message.toString())
    }

    private fun onSuccess(result: Resource.Success<ArrayList<Places>>) {
        placeList = result.data
        binding.progressBar.visibility = View.GONE
        adapter = DepartureAdapter(requireContext(), placeList, this)
        if (binding.editSearchFin.inputType == InputType.TYPE_NULL){
            binding.searchListInit.adapter = adapter
        }else{
            binding.searchListFin.adapter = adapter
        }

    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //createMarker()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        map.setOnMapLongClickListener(this)
        map.setOnMapClickListener(this)
        map.setOnCameraMoveListener(this)
        map.setOnCameraIdleListener(this)
        enableLocation()
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation(){
        if (!::map.isInitialized) return
        if (isLocationPermissionGranted()){
            map.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(activity, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }

    /*@SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            map.isMyLocationEnabled = true
            }else{
                Toast.makeText(activity, "Ve a ajustes y acepta los permisos para activar la localización", Toast.LENGTH_SHORT).show()
            }
            else-> {}
        }
    }*/

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_CODE_LOCATION && (grantResults.size > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            map.isMyLocationEnabled = true
            getLastLocation()
        }else{
            Toast.makeText(activity, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }



    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(activity, "Ve a ajustes y acepta los permisos para activar la localización", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false

    }

    //Método que se llama cuando el usuario pulse en el icono de la localizacion
    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(activity, "Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
    override fun onMapLongClick(position: LatLng) {
        val geocoder = Geocoder(requireContext())
        val address = geocoder.getFromLocation(position.latitude, position.longitude, 1)
        val name = address[0].locality + ", " + address[0].adminArea + ", " + address[0].countryName + " - " + address[0].countryCode
        if (binding.editSearchInit.text.isNullOrEmpty()){
            binding.editSearchInit.setText(name)
            enableInputDestination()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 18f))
            val loc = map.addMarker(
                    MarkerOptions()
                            .position(position)
                            .title(name)

            )
            if (hashMapMarker.get("Init") != null){
                val marker = hashMapMarker.get("Init")
                marker!!.remove()
                hashMapMarker.remove("Init")
            }
            hashMapMarker.put("Init", loc)
            coordinatesInit = LatLng(position.latitude, position.longitude)
            loc.showInfoWindow()

        }else if(binding.editSearchFin.text.isNullOrEmpty()){

            binding.editSearchFin.setText(name)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 18f))
            val loc = map.addMarker(
                    MarkerOptions()
                            .position(position)
                            .title(name)
            )
            if (hashMapMarker.get("Dest") != null){
                val marker = hashMapMarker.get("Dest")
                marker!!.remove()
                hashMapMarker.remove("Dest")
            }
            hashMapMarker.put("Dest", loc)
            coordinatesFin = LatLng(position.latitude, position.longitude)
            loc.showInfoWindow()
        }
        statusBtnNext()
    }

    override fun onLocationClick(location: Places) {
        if (binding.editSearchFin.inputType == InputType.TYPE_NULL){
            setMarkerInit(location)
        }else{
            setMarkerDest(location)
        }
        statusBtnNext()

    }

    private fun setMarkerDest(location: Places) {
        placeList.clear()
        adapter.notifyDataSetChanged()
        binding.searchListFin.visibility = View.GONE
        binding.editSearchFin.setText(location.display_name)
        val coordinates = LatLng(location.latitude.toDouble(), location.longitude.toDouble())
        if (binding.editSearchInit.text.isNullOrEmpty()){
            map.clear()
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18f))
        val loc = map.addMarker(
                MarkerOptions()
                        .position(coordinates)
                        .title(location.display_name)
        )
        if (hashMapMarker.get("Dest") != null){
            val marker = hashMapMarker.get("Dest")
            marker!!.remove()
            hashMapMarker.remove("Dest")
        }
        hashMapMarker.put("Dest", loc)
        coordinatesFin = LatLng(location.latitude.toDouble(), location.longitude.toDouble())
        loc.showInfoWindow()

    }

    private fun setMarkerInit(location: Places) {
        placeList.clear()
        adapter.notifyDataSetChanged()
        binding.searchListInit.visibility = View.GONE
        binding.editSearchInit.setText(location.display_name)
        val coordinates = LatLng(location.latitude.toDouble(), location.longitude.toDouble())
        enableInputDestination()
        if (binding.editSearchFin.text.isNullOrEmpty()){
            map.clear()
        }

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18f))
        val loc = map.addMarker(
                MarkerOptions()
                        .position(coordinates)
                        .title(location.display_name)

        )
        if (hashMapMarker.get("Init") != null){
            val marker = hashMapMarker.get("Init")
            marker!!.remove()
            hashMapMarker.remove("Init")
        }
        hashMapMarker.put("Init", loc)
        coordinatesInit = LatLng(location.latitude.toDouble(), location.longitude.toDouble())
        loc.showInfoWindow()


    }

    override fun onMapClick(p0: LatLng) {
        if (placeList.size > 0) {
            placeList.clear()
            adapter.notifyDataSetChanged()
            binding.searchListInit.visibility = View.GONE
            binding.searchListFin.visibility = View.GONE
        }
    }

    override fun onCameraMove() {
        binding.inputEditInit.visibility = View.GONE
        binding.inputEditFin.visibility = View.GONE
        binding.locationOriginIcon.visibility = View.GONE
        binding.locationFinIcon.visibility = View.GONE
        binding.btnNextMap.visibility = View.GONE
    }

    override fun onCameraIdle() {
        binding.inputEditInit.visibility = View.VISIBLE
        binding.inputEditFin.visibility = View.VISIBLE
        binding.locationOriginIcon.visibility = View.VISIBLE
        binding.locationFinIcon.visibility = View.VISIBLE
        binding.btnNextMap.visibility = View.VISIBLE
    }

    private fun enableInputDestination(){
        binding.editSearchFin.inputType = InputType.TYPE_CLASS_TEXT
        binding.editSearchFin.hint = "Punto de llegada"
        binding.locationFinIcon.isClickable = true
    }

    private fun statusBtnNext() {
        if (!binding.editSearchFin.text.isNullOrEmpty() && !binding.editSearchInit.text.isNullOrEmpty()){
            binding.btnNextMap.setBackgroundResource(R.drawable.button_rounded)
            binding.btnNextMap.isEnabled = true
            viewModelRoute()
        }else{
            binding.btnNextMap.setBackgroundResource(R.drawable.button_rounded_grease)
            binding.btnNextMap.isEnabled = false
        }
    }

    private fun gpsClickListenerInit() {
        binding.locationOriginIcon.setOnClickListener {
            binding.editSearchFin.inputType = InputType.TYPE_NULL
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }else{
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_LOCATION)
            }
        }
    }

    private fun gpsClickListenerFin() {
        binding.locationFinIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }else{
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(OnCompleteListener { task: Task<Location> ->
                val location = task.result
                if (location != null){
                    val geocoder = Geocoder(requireContext())
                    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val name = address[0].locality + ", " + address[0].adminArea + ", " + address[0].countryName + " - " + address[0].countryCode
                    var placeGPS = Places(name, Address(""), location.latitude.toString(), location.longitude.toString())
                    onLocationClick(placeGPS)
                }else{
                    val locationRequest = LocationRequest.create().apply {
                        interval = 1
                        fastestInterval = 50
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        maxWaitTime = 100
                        numUpdates = 1
                    }

                    val locationCallBack = object : LocationCallback(){
                        override fun onLocationResult(p0: LocationResult) {
                            super.onLocationResult(p0)
                            val location1 = p0.lastLocation
                            Log.d("TAG", "" + location1.latitude + location1.longitude)
                            val geocoder = Geocoder(requireContext())
                            val address = geocoder.getFromLocation(location1.latitude, location1.longitude, 1)
                            val name = address[0].locality + ", " + address[0].adminArea + ", " + address[0].countryName + " - " + address[0].countryCode
                            var placeGPS = Places(name, Address(""), location1.latitude.toString(), location1.longitude.toString())
                            onLocationClick(placeGPS)
                        }
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())
                }
            })
        }else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }



}
