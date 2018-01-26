package cz.pepa.runapp.logic

import cz.pepa.runapp.data.DummyFitness


/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object Fit {



    fun getDummyFitnessData(): List<DummyFitness> {
        val one = DummyFitness("Marcel", "Running", 600)
        val two = DummyFitness("Kuba", "Running", 730)
        val three = DummyFitness("Pepa", "Running", 350)

        val dummies = listOf<DummyFitness>(one, two, three)

        return dummies
    }


}