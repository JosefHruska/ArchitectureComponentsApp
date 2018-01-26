package cz.pepa.runapp.ui.main.group

import android.view.View
import cz.pepa.runapp.data.DummyFitness
import cz.pepa.runapp.ui.common.DataBinder
import kotlinx.android.synthetic.main.item_member.view.*

/**
 * Binder for members screen
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
class MembersBinder : DataBinder<DummyFitness>() {

    override fun bind(data: DummyFitness, view: View) {
        view.vName.text = data.name
    }
}