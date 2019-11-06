package com.onct_ict.azukimattya.pictake

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager

class RankingFragment : Fragment() {

    internal var viewPager: ViewPager? = null
    internal var viewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ranking, container, false)
        viewPager = view.findViewById(R.id.viewpager)
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
        viewPager!!.adapter = viewPagerAdapter
        return view
    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    return RankingCollectFragment()
                }
                else -> {
                    return RankingStepFragment()
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> {
                    return "Collected"
                }
                else -> {
                    return "Steps"
                }
            }
        }
    }
}
