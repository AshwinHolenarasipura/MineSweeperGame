package com.ashwinholenarasipura.minesweeper.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ashwinholenarasipura.minesweeper.R
import com.ashwinholenarasipura.minesweeper.databinding.FragmentGamePlayBinding
import com.ashwinholenarasipura.minesweeper.viewmodel.GameplayViewModel
import java.lang.String

class GamePlayFragment : Fragment() {

    private var _binding : FragmentGamePlayBinding? = null
    private val binding get() = _binding!!

    private lateinit var gameplayViewModel: GameplayViewModel

    private var minesCounter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGamePlayBinding.inflate(inflater, container, false)
        gameplayViewModel = ViewModelProvider(this)[GameplayViewModel::class.java]

        gameplayViewModel.init(10, 10, 5) // can be made dynamic, maybe by adding difficulty level.

        startGame();

        return binding.root
    }


    private fun startGame() {

        var tableLayout: TableLayout
        var imageButton: ImageView

        val tvMinesLeft: TextView = binding.minesLeftId

        tvMinesLeft.text = String.format("Mines Left: %d", gameplayViewModel.gameSettings?.mineCount ?: 0)

        minesCounter = gameplayViewModel.numOfMine


        for (i in 0 until (gameplayViewModel.gameSettings?.rows ?: 10)) {
            tableLayout = binding.buttonsPanelId
            val tableRow = TableRow(context)
            tableRow.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            tableRow.gravity = Gravity.CENTER

            for (j in 0 until (gameplayViewModel.gameSettings?.columns ?: 10)) {
                imageButton = ImageView(context)
                imageButton.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )

                imageButton.id = i * (gameplayViewModel.gameSettings?.columns ?: 10) + j

                gameplayViewModel.visited_arr!![i * (gameplayViewModel.gameSettings?.columns ?: 10) + j] = 0

                imageButton.setImageResource(R.drawable.non_clicked_cell)
                imageButton.scaleType = ImageView.ScaleType.FIT_CENTER

                imageButton.setOnClickListener { v: View ->
                    val iView = v as ImageView
                    if (gameplayViewModel.visited_arr!![iView.id] == 2 || gameplayViewModel.visited_arr!![iView.id] == 1) {
                        return@setOnClickListener
                    }

                    if (gameplayViewModel.checkIfMine(iView.id)) {

                        Log.d("Mine", "Mine Detected")

                        gameplayViewModel.setIconToButton(iView, -3)

                        val dialog =
                            AlertDialog.Builder(context)
                        dialog.setCancelable(false)
                        dialog.setIcon(R.mipmap.ic_launcher_round)
                        dialog.setTitle("Game over")
                        dialog.setMessage("Unfortunately, you`ve lost the game!")
                        dialog.setPositiveButton("Ok")
                        { dialog, _ ->  findNavController().navigate(R.id.action_gamePlayFragment_to_mainMenuFragment)}
                        dialog.create()
                        dialog.show()
                    } else {
                        gameplayViewModel.checkNeighbourCells(iView.id, requireView())
                    }
                }

                imageButton.setOnLongClickListener { v: View ->
                    val iView = v as ImageView
                    iView.isClickable = false

                    Log.d("LongClick", "Inside Long Click")

                    if ((gameplayViewModel.visited_arr!![iView.id] != 2) &&
                        gameplayViewModel.visited_arr!![iView.id] != 1 &&
                        minesCounter > 0) {
                        gameplayViewModel.setIconToButton(iView, -1)
                        minesCounter--
                        gameplayViewModel.visited_arr!![iView.id] = 2

                        // if cell has been already long-clicked - return one mine
                    } else if ( gameplayViewModel.visited_arr!![iView.id] == 2) {
                        gameplayViewModel.setIconToButton(iView, -2)
                        minesCounter++
                        gameplayViewModel.visited_arr!![iView.id] = 0
                    }

                    tvMinesLeft.text = String.format("Mines Left: %d", minesCounter)

                    true
                }
                tableRow.addView(imageButton)
            }
            tableLayout.addView(tableRow, TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}