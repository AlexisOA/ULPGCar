package com.alexisoa.ulpgcar.presentation.ui.travels.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class TravelsTabAdapter(fragment: FragmentManager): FragmentPagerAdapter(fragment, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentittleList = ArrayList<String>()


    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentittleList[position]
    }

    fun addFragment(fragment: Fragment, title:String){
        mFragmentList.add(fragment)
        mFragmentittleList.add(title)
    }
}