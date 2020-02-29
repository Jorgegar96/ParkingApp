package com.example.parkingapp.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.parkingapp.R
import com.example.parkingapp.models.Usuario

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager, val usuario: Usuario) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 1){
            return ListarParqueos.newInstance(usuario)
        }else {
            return PlaceholderFragment.newInstance(position + 1)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return (
                if(position == 0){
                    "Parking Layout"
                }else{
                    "Find Free Spots"
                }
                )
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}