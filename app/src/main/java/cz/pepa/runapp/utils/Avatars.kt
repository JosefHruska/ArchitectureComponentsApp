package cz.pepa.runapp.utils

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.support.annotation.ColorRes
import android.support.v4.content.res.ResourcesCompat
import android.widget.ImageView
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import cz.pepa.runapp.R
import cz.pepa.runapp.data.Member
import cz.pepa.runapp.data.User
import io.stepuplabs.settleup.util.extensions.getDrawableCompat
import io.stepuplabs.settleup.util.extensions.toColor

/**
 * Generates avatars for members.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

fun ImageView.loadAvatar(members: List<Member>) {
    if (members.size > 1) {
        this.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.avatar_multiple, null)) // null = no selected theme for this image
    } else {
        throw IllegalArgumentException("Don't use this method for <2 members.")
    }
}

fun ImageView.loadAvatar(member: Member, isCurrentUser: Boolean) {
    if (member.photoUrl != null) {
        val avatarUri: Uri = Uri.parse(member.photoUrl)
        loadPhotoAvatar(this, avatarUri, member.name, isCurrentUser)
    } else {
        setTextAvatar(this, member, isCurrentUser)
    }
}

fun ImageView.loadSmallAvatar(member: Member) {
    if (member.photoUrl != null) {
        val avatarUri: Uri = Uri.parse(member.photoUrl)
        loadPhotoAvatar(this, avatarUri, member.name, isSmallAvatar = true)
    } else {
        setTextAvatar(this, member, isSmallAvatar = true)
    }
}

fun ImageView.loadAvatar(user: User) {
    if (user.photoUrl != null) {
        val avatarUri: Uri = Uri.parse(user.photoUrl)
        loadPhotoAvatar(this, avatarUri, user.name)
    } else {
        setTextAvatar(this, user)
    }
}

private fun loadPhotoAvatar(target: ImageView, avatarUri: Uri, name: String, addMeIndicator: Boolean = false, isSmallAvatar: Boolean = false) {
    target.setImageDrawable(getTextAvatarDrawable(name, R.color.gray_3))
    Glide.with(target.context)
            .load(avatarUri)
            .asBitmap()
            .centerCrop()
            .into(object : BitmapImageViewTarget(target) {
                override fun setResource(resource: Bitmap) {
                    val circleBitmap = getCircularBitmap(resource)
                    if (addMeIndicator) {
                        addMeIndicatorToImage(target, BitmapDrawable(target.context.resources, circleBitmap))
                    } else {
                        target.setImageBitmap(circleBitmap)
                    }
                    if (isSmallAvatar) {
                        target.contentDescription = name
                    }
                }
            })
}

private fun getCircularBitmap(sourceBitmap: Bitmap): Bitmap {
    val circledBitmap = Bitmap.createBitmap(sourceBitmap.width, sourceBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(circledBitmap)
    val color = 0xff424242.toInt()
    val paint = Paint()
    val rect = Rect(0, 0, sourceBitmap.width, sourceBitmap.height)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawCircle(sourceBitmap.width / 2.0f, sourceBitmap.height / 2.0f, sourceBitmap.width / 2.0f, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(sourceBitmap, rect, rect, paint)
    return circledBitmap
}

private fun addMeIndicatorToImage(target: ImageView, circleBitmap: Drawable) {
    val drawableWithMeIndicator = LayerDrawable(arrayOf(circleBitmap,target.getDrawableCompat(R.drawable.me_indicator_background),target.getDrawableCompat(R.drawable.me_indicator)))
    target.setImageDrawable(drawableWithMeIndicator)
}

private fun getTextAvatarDrawable(name: String, @ColorRes backgroundColor: Int): Drawable {
    val letter = name.first().toString()
    return TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig().buildRound(letter, backgroundColor.toColor())
}

private fun setTextAvatar(target: ImageView, member: Member, addMeIndicator: Boolean = false, isSmallAvatar: Boolean = false) {
    val avatarDrawable = getTextAvatarDrawable(member.name, R.color.gray_3)
    if (addMeIndicator) {
        addMeIndicatorToImage(target, avatarDrawable)
    } else {
        target.setImageDrawable(avatarDrawable)
    }
    if (isSmallAvatar) {
        target.contentDescription = member.name
    }
}

private fun setTextAvatar(target: ImageView, user: User) {
    val name = user.name
    target.setImageDrawable(getTextAvatarDrawable(name, R.color.gray_3))
}

fun ImageView.setTextAvatar(character :String, @ColorRes colorRes: Int = R.color.gray_3) {
    this.setImageDrawable(getTextAvatarDrawable(character, colorRes))
}