package com.alexisoa.ulpgcar.presentation.ui.publish

import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.repository.publish.PublishRepositoryImp
import com.alexisoa.ulpgcar.data.repository.vehicles.DataVehiclesImp
import com.alexisoa.ulpgcar.data.repository.vehicles.VehiclesRepoImp
import com.alexisoa.ulpgcar.databinding.FragmentPriceBinding
import com.alexisoa.ulpgcar.domain.interactor.publish.PublishInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.vehicles.VehiclesInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.viewmodels.publish.PublishViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.publish.PublishViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type
import java.util.*


class PriceFragment : Fragment(R.layout.fragment_price) {
    private lateinit var binding: FragmentPriceBinding
    private val viewModelPublish by viewModels<PublishViewModel> { PublishViewModelFactory(VehiclesInteractorImp(VehiclesRepoImp(DataVehiclesImp())), PublishInteractorImp(PublishRepositoryImp())) }
    private var priceDefault : Double = 0.00
    private var max = 0.0
    private var min = 0.0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPriceBinding.bind(view)
        calculatePrice()
        observerBtnPlus()
        observerBtnMinus()
        listenerBack()
        publishTravel()

    }

    private fun calculatePrice(){
        //Get coordinates values
        val latitudeOrigin = prefs.getLatOrigin().toDouble()
        val longitudeOrigin = prefs.getLonOrigin().toDouble()
        val latitudeDest = prefs.getLatDest().toDouble()
        val longitudeDest = prefs.getLonDest().toDouble()
        //Get number of Passengers
        val passengers = prefs.getPassengers().toInt()
        //Set locations
        val loc1 = Location("")
        loc1.setLatitude(latitudeOrigin)
        loc1.setLongitude(longitudeOrigin)

        val loc2 = Location("")
        loc2.setLatitude(latitudeDest)
        loc2.setLongitude(longitudeDest)

        //Calculate distance
        val distanceInMeters =  loc1.distanceTo(loc2).toInt()

        //Calculate price
        var priceByMeter = 1.00
        for (num in 0 .. distanceInMeters){
            if (num % 1000 == 0) priceByMeter += 0.200
        }
        priceDefault = Math.round((priceByMeter/passengers) * 100.00) / 100.00
        binding.price.text = "$priceDefault€"


        max = priceDefault+3.00
        min = priceDefault-3.00

        //max = Math.round((priceDefault+3.00) * 100.00) / 100.00
        //min = Math.round((priceDefault-3.00) * 100.00) / 100.00

        if (min<0.00) min=0.00
    }

    private fun observerBtnPlus(){
        with(binding){
            plus.setOnClickListener {
                priceDefault =  Math.round((priceDefault + 0.50) * 100.0) / 100.0
                if(priceDefault < max){
                    price.text = "$priceDefault€"
                    if(priceDefault > min){
                        substract.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_ulpgc)
                        substract.isEnabled = true
                    }
                }else{
                    plus.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grease)
                    plus.isEnabled = false
                    priceDefault =  Math.round((priceDefault - 0.50) * 100.0) / 100.0
                }
            }
        }
    }

    private fun observerBtnMinus(){
        with(binding){
            substract.setOnClickListener {
                priceDefault =  Math.round((priceDefault- 0.50) * 100.0) / 100.0
                if(priceDefault > min){
                    price.text = "$priceDefault€"
                    if (priceDefault< max){
                        plus.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_ulpgc)
                        plus.isEnabled = true
                    }
                }else{
                    substract.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grease)
                    substract.isEnabled = false
                    priceDefault =  Math.round((priceDefault + 0.50) * 100.0) / 100.0
                }
            }
        }

    }

    private fun listenerBack(){
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun publishTravel(){
        with(binding){
            next.setOnClickListener {
                val gson = Gson()
                val json: String = prefs.getJson()
                val type: Type = object : TypeToken<ArrayList<com.alexisoa.ulpgcar.data.model.LatLng?>?>(){}.type
                val pointsList: ArrayList<com.alexisoa.ulpgcar.data.model.LatLng> = gson.fromJson(json, type)
                prefs.savePrice(priceDefault.toString())
                val dataTravels = Travel(prefs.getLatOrigin().toDouble(), prefs.getLonOrigin().toDouble(), prefs.getLatDest().toDouble(),
                prefs.getLonDest().toDouble(), prefs.getNameOrigin(), prefs.getNameDest(), getDateTimeinMillis(), prefs.getHourFinished().toLong(),
                prefs.getPassengers().toInt(), prefs.getVehicleBrand(), prefs.getVehicleModel(), prefs.getPrice(), prefs.getPrefEat(), prefs.getPrefSmoke(),
                prefs.getPrefMusic(), routePoints = pointsList)

                viewModelPublish.createAnnouncement(dataTravels)
                        .observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
                            when(result){
                                is Resource.Loading->onLoading()
                                is Resource.Success->onSuccess()
                                is Resource.Failure->onFailed(result)
                            }
                        })
            }
        }
    }

    private fun FragmentPriceBinding.onLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun FragmentPriceBinding.onFailed(result: Resource.Failure<Boolean>) {
        progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
    }

    private fun FragmentPriceBinding.onSuccess() {
        progressBar.visibility = View.GONE
        prefs.wipePublish()
        findNavController().navigate(R.id.action_navigation_price_fragment_to_navigation_travels)
    }

    private fun getDateTimeinMillis(): Long{
        val calendarDateTime = Calendar.getInstance()
        val dateMillis = prefs.getDate().toLong()
        val timeMillis = prefs.getHourStart().toLong()
        val calendarDate = Calendar.getInstance()
        val calendarTime = Calendar.getInstance()
        calendarDate.timeInMillis = dateMillis
        calendarTime.timeInMillis = timeMillis

        calendarDateTime.set(Calendar.YEAR, calendarDate.get(Calendar.YEAR))
        calendarDateTime.set(Calendar.MONTH, calendarDate.get(Calendar.MONTH))
        calendarDateTime.set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH))
        calendarDateTime.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY))
        calendarDateTime.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE))
        calendarDateTime.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND))
        return calendarDateTime.timeInMillis
    }





}