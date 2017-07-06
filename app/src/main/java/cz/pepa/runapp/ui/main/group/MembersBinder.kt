package cz.pepa.runapp.ui.main.group

import android.view.View
import cz.pepa.runapp.data.DummyFittnes
import cz.pepa.runapp.ui.common.DataBinder
import kotlinx.android.synthetic.main.item_member.view.*

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
class MembersBinder : DataBinder<DummyFittnes>() {

    override fun bind(data: DummyFittnes, view: View) {
        view.vName.text = data.name
    }
}