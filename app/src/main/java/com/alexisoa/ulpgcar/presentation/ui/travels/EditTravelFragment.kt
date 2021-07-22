package com.alexisoa.ulpgcar.presentation.ui.travels

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.model.Vehicle
import com.alexisoa.ulpgcar.data.repository.publish.PublishRepositoryImp
import com.alexisoa.ulpgcar.data.repository.vehicles.DataVehiclesImp
import com.alexisoa.ulpgcar.data.repository.vehicles.VehiclesRepoImp
import com.alexisoa.ulpgcar.databinding.FragmentEditTravelBinding
import com.alexisoa.ulpgcar.domain.interactor.publish.PublishInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.vehicles.VehiclesInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.publish.DatePickerFragment
import com.alexisoa.ulpgcar.presentation.viewmodels.publish.PublishViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.publish.PublishViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.gms.maps.model.LatLng
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_edit_travel.*
import kotlinx.android.synthetic.main.fragment_edit_travel.progressBar
import kotlinx.android.synthetic.main.fragment_edit_travel.timeInit
import java.lang.reflect.Type
import java.util.*
import java.util.regex.Pattern

class EditTravelFragment : Fragment(R.layout.fragment_edit_travel) {
    private lateinit var binding: FragmentEditTravelBinding
    private lateinit var travel: Travel
    private lateinit var calendarDate: Calendar
    private lateinit var calendarTimeFin: Calendar
    private lateinit var calendarTimeInit : Calendar
    private var latitude_or : Double = 0.0
    private var longitude_or : Double = 0.0
    private var latitude_dest : Double = 0.0
    private var longitude_dest : Double = 0.0
    private val viewModelPublish by viewModels<PublishViewModel> { PublishViewModelFactory(
        VehiclesInteractorImp(VehiclesRepoImp(DataVehiclesImp())), PublishInteractorImp(
            PublishRepositoryImp()
        )
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            travel = it.getParcelable("travel")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditTravelBinding.bind(view)
        calendarDate = Calendar.getInstance()
        calendarTimeFin = Calendar.getInstance()
        calendarTimeInit = Calendar.getInstance()
        refillEdit()
        listenerModelCar()
        listenerDate()
        listenerTimeInit()
        listenerTimeFin()
        dataFindNavController()
        listenerUpdate()
        listenerBack()
        listenerPopUp()
    }



    private fun listenerPopUp() {
            binding.popUpLocation.setOnClickListener {
                val popupMenu : PopupMenu = PopupMenu(requireContext(), binding.popUpLocation)
                popupMenu.menuInflater.inflate(R.menu.menu_modify, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        item -> when(item.itemId){
                    R.id.modifyItem -> goToMap()
                }
                    true
                })
                popupMenu.show()
            }
    }

    private fun goToMap() {
        prefs.saveEdit("edit")
        findNavController().navigate(R.id.action_navigation_edit_travels_to_navigations_maps)

    }

    private fun listenerBack() {
        binding.back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun showDialogUpdate(){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = "Tu publicación se actualizará con los datos que has introducido."
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {
            updateAnnouncement()
            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }

    private fun updateAnnouncement() {
        val travelEdit = Travel(latitude_or, longitude_or, latitude_dest, longitude_dest, binding.originPublishEdit.text.toString(),
                binding.destinationPublishEdit.text.toString(), getDateTimeinMillis(), calendarTimeFin.timeInMillis, binding.passenger.text.toString().toInt(),
                binding.plateVehicle.text.toString(), binding.brandVehicle.text.toString(), travel.cost, travel.eatPermission, travel.smokePermission, travel.musicPermission,
                travelId = travel.travelId, routePoints = travel.routePoints)

        viewModelPublish.updateAnnouncement(travelEdit)
                .observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
                    when(result){
                        is Resource.Loading->onLoading()
                        is Resource.Success->onSuccess()
                        is Resource.Failure->onFailed(result)
                    }
                })
    }

    private fun refillEdit(){
        with(binding){
            calendarDate.timeInMillis = travel.dateTime
            calendarTimeInit.timeInMillis = travel.dateTime
            calendarTimeFin.timeInMillis = travel.timeFinish
            val currentMonth = calendarDate.get(Calendar.MONTH)+1
            val year = calendarDate.get(Calendar.YEAR)
            val hourComplete = String.format("%02d:%02d", calendarTimeInit.get(Calendar.HOUR_OF_DAY), calendarTimeInit.get(Calendar.MINUTE))
            val hourFinish = String.format("%02d:%02d", calendarTimeFin.get(Calendar.HOUR_OF_DAY), calendarTimeFin.get(Calendar.MINUTE))
            latitude_or = travel.lat_init
            longitude_or = travel.long_init
            latitude_dest = travel.lat_dest
            longitude_dest = travel.long_dest
            originPublishEdit.setText(cutText(travel.nameOrigin))
            destinationPublishEdit.setText(cutText(travel.nameDestination))
            plateVehicle.setText(travel.plateVehicle)
            brandVehicle.setText(travel.modelVehicle)
            datePublish.setText(cutText("${calendarDate.get(Calendar.DAY_OF_MONTH)}/$currentMonth/$year"))
            passenger.setText(travel.passengers.toString())
            timeInit.setText(cutText(hourComplete))
            timeFin.setText(cutText(hourFinish))

        }
    }

    private fun listenerDate() {
        binding.datePublish.setOnClickListener {
            println(calendarDate.timeInMillis)
            val fragmentDatePicker = DatePickerFragment{day, month, year -> onDateSelected(day, month, year)}
            fragmentDatePicker.show(childFragmentManager, "DatePicker")
        }
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        calendarDate.set(year, month, day)
        val currentMonth = calendarDate.get(Calendar.MONTH)+1
        binding.datePublish.setText(cutText("${calendarDate.get(Calendar.DAY_OF_MONTH)}/$currentMonth/$year"))
    }

    private fun listenerTimeInit(){
        binding.timeInit.setOnClickListener {
            val hora = calendarTimeInit.get(Calendar.HOUR_OF_DAY)
            val minute = calendarTimeInit.get(Calendar.MINUTE)
            val timerPickerDialog = TimePickerDialog(activity, R.style.datePickerTheme, TimePickerDialog.OnTimeSetListener() { timePicker: TimePicker, hours: Int, minutes: Int ->
                calendarTimeInit.set(Calendar.HOUR_OF_DAY, hours)
                calendarTimeInit.set(Calendar.MINUTE, minutes)
                timeInit.setText(cutText(String.format("%02d:%02d", calendarTimeInit.get(Calendar.HOUR_OF_DAY), calendarTimeInit.get(Calendar.MINUTE))))
            }, hora, minute, true)
            timerPickerDialog.show()
        }
    }

    private fun listenerTimeFin(){
        binding.timeFin.setOnClickListener {
            val hora = calendarTimeFin.get(Calendar.HOUR_OF_DAY)
            val minute = calendarTimeFin.get(Calendar.MINUTE)
            val timerPickerDialog = TimePickerDialog(activity, R.style.datePickerTheme, TimePickerDialog.OnTimeSetListener() { timePicker: TimePicker, hours: Int, minutes: Int ->
                calendarTimeFin.set(Calendar.HOUR_OF_DAY, hours)
                calendarTimeFin.set(Calendar.MINUTE, minutes)
                timeFin.setText(cutText(String.format("%02d:%02d", calendarTimeFin.get(Calendar.HOUR_OF_DAY), calendarTimeFin.get(Calendar.MINUTE))))
            }, hora, minute, true)
            timerPickerDialog.show()
        }
    }


    private fun cutText(text: String):String{
        if(text.length > 30) return text.substring(0,30) + "..."
        return text
    }

    private fun listenerModelCar(){
        with(binding){
            brandVehicle.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_edit_travels_to_navigation_modelCar)
            }
        }
    }


    private fun dataFindNavController(){
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Vehicle>("brandCar")
                ?.observe(viewLifecycleOwner, Observer { brandCar->
                    prefs.saveVehicleModel(brandCar.makeName)
                    binding.brandVehicle.setText(cutText(brandCar.makeName))
                })

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("prefs")
                ?.observe(viewLifecycleOwner, Observer { result->
                    latitude_or = prefs.getLatOrigin().toDouble()
                    longitude_or = prefs.getLonOrigin().toDouble()
                    latitude_dest = prefs.getLatDest().toDouble()
                    longitude_dest = prefs.getLonDest().toDouble()
                    binding.originPublishEdit.setText(prefs.getNameOrigin())
                    binding.destinationPublishEdit.setText(prefs.getNameDest())
                    val gson = Gson()
                    val json: String = prefs.getJson()
                    val type: Type = object : TypeToken<ArrayList<com.alexisoa.ulpgcar.data.model.LatLng?>?>(){}.type
                    val pointsList: ArrayList<com.alexisoa.ulpgcar.data.model.LatLng> = gson.fromJson(json, type)
                    travel.routePoints = pointsList
                })
    }

    private fun listenerUpdate(){
        binding.update.setOnClickListener {
            if (!validPlateCar(binding.plateVehicle.text.toString())) binding.plateVehicle.setError("Por favor, introduzca una matrícula válida")
            if (!validPassengers(binding.passenger.text.toString())) binding.passenger.setError("Introduzca un número de pasajeros válido")
            if (validPlateCar(binding.plateVehicle.text.toString()) && validPassengers(binding.passenger.text.toString())){
                showDialogUpdate()
            }
        }
    }


    private fun onLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun onFailed(result: Resource.Failure<Boolean>) {
        progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onSuccess() {
        progressBar.visibility = View.GONE
        prefs.wipePublish()
        findNavController().navigate(R.id.action_navigation_edit_travels_to_navigation_travels)
    }

    private fun validPlateCar(plate: String): Boolean{
        return Pattern.compile("^[0-9]{1,4}(?!.*(LL|CH))[ABCDFGHJKLMNPRSTVWXYZ]{3}").matcher(plate).matches()
    }

    private fun validPassengers(plate: String): Boolean{
        return Pattern.compile("^[1-4]{1}").matcher(plate).matches()
    }

    private fun getDateTimeinMillis(): Long{
        val calendarDateTime = Calendar.getInstance()
        val dateMillis = calendarDate.timeInMillis
        val timeMillis = calendarTimeInit.timeInMillis
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