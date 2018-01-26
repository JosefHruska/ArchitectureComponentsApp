package cz.pepa.runapp.ui.main.goal

import cz.pepa.runapp.data.Type
import cz.pepa.runapp.ui.base.BaseController

/**
 * Controller for [AddGoalActivity]
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
interface AddGoalController: BaseController {

    fun setSelectedType(selectedType: Type)

    fun setAverageValues(values: List<Float>)

    fun setupSummaryText(text: String)

    fun setGoalRating(rating: String)

    fun resetTargetValueSelector()

    fun seTargetValueSelectorPercentage(percentage: Int)
}