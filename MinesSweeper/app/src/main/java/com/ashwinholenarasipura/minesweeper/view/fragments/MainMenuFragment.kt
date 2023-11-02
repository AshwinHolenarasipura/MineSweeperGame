package com.ashwinholenarasipura.minesweeper.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ashwinholenarasipura.minesweeper.R
import com.ashwinholenarasipura.minesweeper.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {

    private var _binding : FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        binding.playButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_gamePlayFragment)
        }
       return binding.root
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    }