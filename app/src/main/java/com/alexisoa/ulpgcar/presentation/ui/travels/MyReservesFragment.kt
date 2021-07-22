package com.alexisoa.ulpgcar.presentation.ui.travels

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexisoa.ulpgcar.R
import com.alexisoa.ulpgcar.data.model.Chat
import com.alexisoa.ulpgcar.data.model.Reserve
import com.alexisoa.ulpgcar.data.model.Travel
import com.alexisoa.ulpgcar.data.repository.reserves.ReservesRepositoryImp
import com.alexisoa.ulpgcar.data.repository.travels.TravelsRepositoryImp
import com.alexisoa.ulpgcar.databinding.FragmentMyPublishBinding
import com.alexisoa.ulpgcar.databinding.FragmentMyTravelsBinding
import com.alexisoa.ulpgcar.domain.interactor.reserves.ReservesInteractorImp
import com.alexisoa.ulpgcar.domain.interactor.travels.TravelsInteractorImp
import com.alexisoa.ulpgcar.presentation.preferenced.UserSharedApplication.Companion.prefs
import com.alexisoa.ulpgcar.presentation.ui.messages.adapter.ChatAdapter
import com.alexisoa.ulpgcar.presentation.ui.travels.adapter.ReservesAdapter
import com.alexisoa.ulpgcar.presentation.ui.travels.adapter.TravelsAdapter
import com.alexisoa.ulpgcar.presentation.viewmodels.reserves.ReservesViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.reserves.ReservesViewModelFactory
import com.alexisoa.ulpgcar.presentation.viewmodels.travels.TravelsViewModel
import com.alexisoa.ulpgcar.presentation.viewmodels.travels.TravelsViewModelFactory
import com.alexisoa.ulpgcar.valueobject.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyReservesFragment : Fragment(R.layout.fragment_my_travels), ReservesAdapter.OnReserveClickListener{
    private lateinit var binding: FragmentMyTravelsBinding
    private val viewModelReserve by viewModels<ReservesViewModel> { ReservesViewModelFactory(
            ReservesInteractorImp(ReservesRepositoryImp())
    ) }

    private val reserveList = ArrayList<Reserve>()
    private lateinit var adapter: ReservesAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyTravelsBinding.bind(view)
        checkBadge()
        setupRecycletView()
        loadMyTravelsReserved()
        adapter = ReservesAdapter(requireContext(), reserveList, this)
        onChangedReserveSnapshotCollection()
    }

    private fun checkBadge() {
        if (prefs.getNotiRequest().equals("yes")){
            prefs.saveNotiRequest("no")
            setBadge()
        }
    }

    private fun onChangedReserveSnapshotCollection() {
        viewModelReserve.onChangedReserveSnapshot(::onChangedReserve).observe(viewLifecycleOwner, Observer { result: Resource<Boolean> ->
            println("cargado")
        })
    }

    private fun setupRecycletView(){
        with(binding){
            recyclerReserves.layoutManager = LinearLayoutManager(requireContext())
            recyclerReserves.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun loadMyTravelsReserved() {
        viewModelReserve.getAllReserves().observe(viewLifecycleOwner, Observer { result: Resource<ArrayList<Reserve>> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess(result)
                is Resource.Failure->onFailed(result)
            }

        })
    }

    private fun onLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun onFailed(result: Resource.Failure<ArrayList<Reserve>>) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(activity, " ${result.exception.message}", Toast.LENGTH_SHORT).show()
        Log.e("ERROR", result.exception.message.toString())
    }

    private fun onChangedReserve(r : Reserve) {
        if (context != null && !r.statusReserve.equals("Pendiente")) {
            if (!reserveList.contains(r)) {
                reserveList.add(r)
                adapter.notifyItemInserted(reserveList.indexOf(r))
                binding.recyclerReserves.scrollToPosition(reserveList.indexOf(r))
            } else {
                val index = reserveList.indexOf(r)
                reserveList[index] = r
                adapter.notifyItemChanged(reserveList.indexOf(r))
            }
            binding.recyclerReserves.scrollToPosition(reserveList.indexOf(r))
        }
    }

    private fun onSuccess( result : Resource.Success<ArrayList<Reserve>>) {
        binding.progressBar.visibility = View.GONE
        cleanBadge()
        //adapter.notifyDataSetChanged()
        reserveList.clear()
        reserveList.addAll(result.data)
        //val tmp = chatList.distinct()

        //chatList.addAll(tmp)
        adapter = ReservesAdapter(requireContext(), reserveList, this)
        adapter.notifyDataSetChanged()
        binding.recyclerReserves.adapter = adapter

    }

    override fun onReserveDetailsClick(reserve: Reserve) {
        if (reserve.statusReserve.equals("Rechazada") || reserve.statusReserve.equals("Cancelada")){
            showDialogDelete(reserve)
        }else{
            val bundle = Bundle()
            bundle.putParcelable("reserve", reserve)
            findNavController().navigate(R.id.action_navigation_travels_to_myReservesDetailsFragment, bundle)
        }

    }

    private fun showDialogDelete(reserve: Reserve) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view : View = inflater.inflate(R.layout.dialog_yes_or_no, null)
        val textTitle = view.findViewById<TextView>(R.id.textTitle)
        textTitle.text = "Reserva rechazada"
        val text = view.findViewById<TextView>(R.id.textDescriptionDialog)
        text.text = "Su reserva ha sido rechazada, pulse aceptar para eliminarla de su lista actual."
        builder.setView(view)
        val dialog : AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val buttonConfirm = view.findViewById<Button>(R.id.btn_yes)
        buttonConfirm.setOnClickListener {
            deleteReserveReject(reserve)
            dialog.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.btn_no)
        buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun deleteReserveReject(reserve: Reserve) {
        viewModelReserve.deleteReserve(reserve).observe(viewLifecycleOwner, Observer { result: Resource<ArrayList<Reserve>> ->
            when(result){
                is Resource.Loading->onLoading()
                is Resource.Success->onSuccess(result)
                is Resource.Failure->onFailed(result)
            }

        })
    }

    private fun cleanBadge(){
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_travels)
        badge.isVisible = false
    }

    private fun setBadge(){
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        var badge = bottomNavigation.getOrCreateBadge(R.id.navigation_travels)
        badge.isVisible = true
    }


}