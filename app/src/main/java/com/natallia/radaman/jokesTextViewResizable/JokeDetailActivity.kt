package com.natallia.radaman.jokesTextViewResizable

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.dialog_add_text.view.*

class JokeDetailActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_JOKE = "EXTRA_JOKE"
        private const val PICK_IMAGE_REQUEST = 1
        fun newIntent(context: Context, joke: String) =
            Intent(context, JokeDetailActivity::class.java).apply { putExtra(EXTRA_JOKE, joke) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        jokeDescription.text = intent.getStringExtra(EXTRA_JOKE)
        jokeDescription.setOnTouchListener(getTouchListener())
        jokeBackground.setOnClickListener { showGallery() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            jokeBackground.setImageBitmap(bitmap)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        when (id) {
            R.id.share -> {
                shareAction()
                return true
            }
            R.id.text -> {
                showAddTextDialog()
                return true
            }
            R.id.background -> {
                showGallery()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareAction() {
        ShareViewHelper.share(this, container)
    }

    private fun showAddTextDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_text, null)
        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(getString(R.string.ok)) { _, _ -> addText(view.input.text.toString()) }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun addText(string: String) {
        if (string.isNotBlank()) {
            val textView = buildTextView(string)
            container.addView(textView)
        }
    }

    private fun buildTextView(string: String): TextView {
        val txtColor = ContextCompat.getColor(this, android.R.color.white)
        val bgColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        val padding = resources.getDimensionPixelSize(R.dimen.joke_margin)
        val height = resources.getDimensionPixelSize(R.dimen.joke_height)

        return TextView(this).apply {
            text = string
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
            )
            setPadding(padding, padding, padding, padding)
            layoutParams = params
            gravity = Gravity.CENTER_VERTICAL
            setTextColor(txtColor)
            setBackgroundColor(bgColor)
            setOnTouchListener(getTouchListener())
            TextViewCompat.setAutoSizeTextTypeUniformWithPresetSizes(
                this,
                intArrayOf(12, 16, 20, 24, 28),
                TypedValue.COMPLEX_UNIT_SP
            )
        }
    }

    private fun getTouchListener(): TouchListenerImpl {
        val min = resources.getDimensionPixelSize(R.dimen.joke_height_min)
        return TouchListenerImpl(min, min)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    private fun showGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_picture)),
            PICK_IMAGE_REQUEST
        )
    }
}
