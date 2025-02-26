package com.inncreator.testpoints.features.points

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.inncreator.testpoints.R
import com.inncreator.testpoints.features.points.common.ChartSettings
import com.inncreator.testpoints.data.models.Point
import com.inncreator.testpoints.features.points.adapters.PointsAdapter
import com.inncreator.testpoints.features.points.common.PointsUiState
import com.inncreator.testpoints.domain.models.ProcessedPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PointsFragment : Fragment() {

    private val viewModel: PointsViewModel by viewModels()
    private val args: PointsFragmentArgs by navArgs()

    private lateinit var recyclerView: RecyclerView
    private lateinit var chart: LineChart
    private lateinit var progressBar: ProgressBar

    private var pointsAdapter: PointsAdapter? = null

    private val measurementTextPaint by lazy { TextView(requireContext()).paint }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_points, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_table)
        chart = view.findViewById(R.id.chart)
        progressBar = view.findViewById(R.id.progress_bar)

        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)
            ?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val points: List<ProcessedPoint> = args.points.toList()
        viewModel.processPoints(points)

        setupMenu()
        observeUiState()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.chart_settings_menu, menu)
            }

            // todo отказаться от менюшки, перейти на попап, добавить дебаунс и работать с вью напрямую
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                menuItem.isChecked = !menuItem.isChecked
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }

                    R.id.smooth_lines -> {
                        viewModel.updateChartSettings { it.copy(smoothLines = menuItem.isChecked) }
                        true
                    }

                    R.id.remove_circles -> {
                        viewModel.updateChartSettings { it.copy(removeCircles = menuItem.isChecked) }
                        true
                    }

                    R.id.remove_values -> {
                        viewModel.updateChartSettings { it.copy(removeValues = menuItem.isChecked) }
                        true
                    }

                    R.id.reduce_points -> {
                        viewModel.updateChartSettings { it.copy(reducePoints = menuItem.isChecked) }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is PointsUiState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                            chart.visibility = View.GONE
                        }

                        is PointsUiState.Init -> {
                            progressBar.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            chart.visibility = View.VISIBLE
                            setupRecyclerView(state.processedPoints, state.minWidth)
                            updateChartData(state.processedPoints, state.chartSettings)
                        }

                        is PointsUiState.Update -> {
                            updateChartData(state.processedPoints, state.chartSettings)
                        }

                        is PointsUiState.Error -> {}
                    }
                }
            }
        }
    }

    /**
    todo Вызывается только один раз, не поддерживает обновление данных, соответсвует бизнес логике
     */
    private fun setupRecyclerView(points: List<ProcessedPoint>, minWidth: Triple<Int, Int, Int>) {
        val isLandscape = resources.configuration.orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE

        val layoutManager = LinearLayoutManager(
            requireContext(),
            if (isLandscape) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL,
            false
        )
        recyclerView.layoutManager = layoutManager

        if (pointsAdapter == null) {
            pointsAdapter = PointsAdapter(points, calculateMinWidth(minWidth))
            recyclerView.adapter = pointsAdapter
        }

        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        )
    }

    /**
     * todo вынести логику формирования данных из фрагмента
     *  Подумать над логикой [settings.reducePoints] при необходимости
     */
    private fun updateChartData(points: List<ProcessedPoint>, settings: ChartSettings) {
        val filteredPoints = if (settings.reducePoints) {
            points.filterIndexed { index, _ -> index % 5 == 0 }
        } else {
            points
        }
        val entries = filteredPoints.map { Entry(it.x, it.y) }
        val dataSet = LineDataSet(entries, "label").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = ContextCompat.getColor(requireContext(), R.color.teal_200)
            valueTextSize = 10f

            if (settings.smoothLines) mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            if (settings.removeCircles) setDrawCircles(false)
            if (settings.removeValues) setDrawValues(false)

            fillAlpha = 50
        }

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
        }
        chart.axisLeft.apply {
            setDrawGridLines(true)
            gridColor = Color.LTGRAY
        }
        chart.axisRight.isEnabled = false

        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    /**
     * Решение для ровной таблице вертикального ресайкла
     */
    private fun calculateMinWidth(minWidthRaw: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
        val sampleText = "X".repeat(minWidthRaw.first)
        val sampleXText = "X".repeat(minWidthRaw.second)
        val sampleYText = "X".repeat(minWidthRaw.third)
        val minCountWidth = measurementTextPaint.measureText(sampleText).toInt()
        val minXWidth = measurementTextPaint.measureText(sampleXText).toInt()
        val minYWidth = measurementTextPaint.measureText(sampleYText).toInt()
        return Triple(minCountWidth, minXWidth, minYWidth)
    }
}