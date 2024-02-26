package com.myungwoo.shoppingmall_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.FragmentBookmarkBinding
import com.myungwoo.shoppingmall_app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment2_to_tipFragment)
        }

        binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment2_to_talkFragment)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment2_to_bookmarkFragment)
        }

        binding.storeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment2_to_shopFragment)
        }

        val viewToShake = binding.homeExplanation
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake_animation)
        viewToShake.startAnimation(shake)

        binding.shoppingBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment2_to_shopFragment)
        }

        binding.beautyBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment2_to_tipFragment)
        }

        binding.boardBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment2_to_talkFragment)
        }

        return binding.root
    }
}