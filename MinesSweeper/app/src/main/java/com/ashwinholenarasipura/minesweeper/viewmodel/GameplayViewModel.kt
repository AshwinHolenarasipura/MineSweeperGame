package com.ashwinholenarasipura.minesweeper.viewmodel

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import com.ashwinholenarasipura.minesweeper.model.Data
import java.util.LinkedList
import java.util.Queue
import com.ashwinholenarasipura.minesweeper.R

class GameplayViewModel : ViewModel() {

    var gameSettings: Data? = null

    var numOfMine : Int = 0

    private var mines_arr: IntArray? = null

    var visited_arr: IntArray? = null

    fun init(rows : Int, columns : Int, mineCount : Int){

       gameSettings =  Data(rows, columns, mineCount, false)

        numOfMine = gameSettings?.mineCount ?: 10
        mines_arr = generateMines((gameSettings?.columns ?: 10) * (gameSettings?.rows ?: 10))
        visited_arr = IntArray((gameSettings?.columns ?: 10) * (gameSettings?.rows ?: 10))
    }

    private fun generateMines(square: Int): IntArray? {
        mines_arr = IntArray(numOfMine)
        val min = 1
        val max = square - 1
        for (i in 0 until numOfMine) {
            var isUnique = false
            while (!isUnique) {
                val randNum = (Math.random() * (max - min + 1) + min).toInt()
                for (k in 0..i) {
                    if (mines_arr!![k] == randNum) {
                        isUnique = false
                        break
                    }
                    if (k == i) {
                        mines_arr!![i] = randNum
                        isUnique = true
                    }
                }
            }
        }

        for (j in 0 until numOfMine) {
            Log.d("CREATED_ARR", "" + mines_arr!![j])
        }
        return mines_arr
    }

    fun checkIfMine(buttonNumInt: Int): Boolean {
        for (i in 0 until numOfMine) {
            if (buttonNumInt == mines_arr!![i]) {
                return true
            }
        }
        return false
    }

    fun checkNeighbourCells(butNum: Int, view : View) {
        var butNum = butNum

        Log.d("butNum", "butnumber is $butNum")

        val queue: Queue<Int> = LinkedList()
        queue.add(butNum)
        visited_arr!![butNum] = 1

        var rows: Int = (gameSettings?.rows ?: 10)
        var cols: Int = (gameSettings?.columns ?: 10)

        Log.d("Row", "Rows is $rows")
        Log.d("Col", "Cols is $cols")

        val arrofneighbours = IntArray(8)

        while (queue.size > 0) {
            butNum = queue.element()
            arrofneighbours[0] = butNum - cols
            arrofneighbours[1] = butNum - cols + 1
            arrofneighbours[2] = butNum + 1
            arrofneighbours[3] = butNum + cols + 1
            arrofneighbours[4] = butNum + cols
            arrofneighbours[5] = butNum + cols - 1
            arrofneighbours[6] = butNum - 1
            arrofneighbours[7] = butNum - cols - 1
            queue.remove()

            for (i in 0..7) {
                val res: Int = checkNeighbourCell(butNum, arrofneighbours[i])
                if (res == 0 &&
                    visited_arr!![arrofneighbours[i]] == 0
                ) {
                    queue.add(arrofneighbours[i])
                    visited_arr!![arrofneighbours[i]] = 1
                } else if (res == 1 && visited_arr!![arrofneighbours[i]] == 0) {
                    visited_arr!![arrofneighbours[i]] = 1
                }
            }
        }
        var minesInNeighbourCells = 0

        for (j in 0 until rows * cols) {
            if (visited_arr!![j] == 1) {
                Log.d("VisitedArr", "Inside if of Visited Array")
                if (checkForMinesCount(j - cols, j - cols) == 1) {
                    minesInNeighbourCells++
                }
                if (((j % cols) != (cols - 1)) && checkForMinesCount(j - cols + 1, j - cols + 1) == 1) {
                    minesInNeighbourCells++
                }
                if (((j % cols) != (cols - 1)) && checkForMinesCount(j + 1, j + 1) == 1) {
                    minesInNeighbourCells++
                }
                if (((j % cols) != (cols - 1)) && checkForMinesCount(j + cols + 1, j + cols + 1) == 1) {
                    minesInNeighbourCells++
                }
                if (checkForMinesCount(j + cols, j + cols) == 1) {
                    minesInNeighbourCells++
                }
                if (((j % cols) != 0 ) && checkForMinesCount(j + cols - 1, j + cols - 1) == 1) {
                    minesInNeighbourCells++
                }
                if (((j % cols) != 0) && checkForMinesCount(j - 1, j - 1) == 1) {
                    minesInNeighbourCells++
                }
                if (((j % cols) != 0) && checkForMinesCount(j - cols - 1, j - cols - 1) == 1) {
                    minesInNeighbourCells++
                }
                var imageView: ImageView = view.findViewById(j)
                setIconToButton(imageView, minesInNeighbourCells)
            }
            minesInNeighbourCells = 0
        }
    }

    private fun checkNeighbourCell(firstParam: Int, butNum: Int): Int {
        if (!checkIfValidCoord(firstParam, butNum)) {
            return 10
        }
        for (i in 0 until numOfMine) {
            if (mines_arr!![i] == butNum) {
                return 2
            }
        }
        val cols: Int = (gameSettings?.columns ?: 10)
        val arr = IntArray(8)
        arr[0] = butNum - cols
        arr[1] = butNum - cols + 1
        arr[2] = butNum + 1
        arr[3] = butNum + cols + 1
        arr[4] = butNum + cols
        arr[5] = butNum + cols - 1
        arr[6] = butNum - 1
        arr[7] = butNum - cols - 1
        for (i in 0 until numOfMine) {
            for (j in 0..7) {
                if (checkIfValidCoord(butNum, arr[j]) && mines_arr!![i] == arr[j]) {
                    return 1
                }
            }
        }
        return 0
    }

    private fun checkForMinesCount(firstParam: Int, num: Int): Int {
        if (!checkIfValidCoord(firstParam, num)) {
            return 10
        }
        for (i in 0 until numOfMine) {
            if (mines_arr!![i] == num) {
                return 1
            }
        }
        return 0
    }

    private fun checkIfValidCoord(firstParam: Int, num: Int): Boolean {

        // transform num into x and y coordinates
        val x: Int = num % (gameSettings?.columns ?: 10)
        val y: Int = num / (gameSettings?.columns ?: 10)
        val x_prev: Int = firstParam % (gameSettings?.columns ?: 10)

        // check if cell is out of range
        return x >= 0 && y >= 0 && x < (gameSettings?.columns ?: 10)&& y < (gameSettings?.rows ?: 10) &&
                (x != 0 || x_prev != (gameSettings?.columns ?: 10) - 1) && (x != (gameSettings?.columns ?: 10) - 1 || x_prev != 0)
    }

    fun setIconToButton(imageView: ImageView?, minesNum: Int) {

        Log.d("MineNum", "Mine Numeber -> $minesNum")

        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!")
            return
        }

        when(minesNum){
            0 -> imageView.setImageResource(R.drawable.empty_cell)
            1 -> imageView.setImageResource(R.drawable.digit_1)
            2 -> imageView.setImageResource(R.drawable.digit_2)
            3 -> imageView.setImageResource(R.drawable.digit_3)
            4 -> imageView.setImageResource(R.drawable.digit_4)
            5 -> imageView.setImageResource(R.drawable.digit_5)
            6 -> imageView.setImageResource(R.drawable.digit_6)
            7 -> imageView.setImageResource(R.drawable.digit_7)
            8 -> imageView.setImageResource(R.drawable.digit_8)
            -1 -> imageView.setImageResource(R.drawable.flag)
            -2 -> imageView.setImageResource(R.drawable.non_clicked_cell)
            -3 -> imageView.setImageResource(R.drawable.mine_clicked)
        }
    }
}