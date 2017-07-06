package cz.pepa.runapp.ui.main.group

import cz.pepa.runapp.logic.Fit
import cz.pepa.runapp.ui.base.BaseViewModel

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class GroupViewModel: BaseViewModel() {

    fun loadDummyFitness() {
        mMembers.value = Fit.getDummyFitnessData()
    }
}