package com.ssafy.likloud.ui.upload

import android.os.Bundle
import android.view.View
import com.ssafy.likloud.R
import com.ssafy.likloud.base.BaseDialog
import com.ssafy.likloud.databinding.FragmentUploadBinding
import com.ssafy.likloud.databinding.ModalChooseGalleryCameraBinding


class AICheckingDialog(
//    private val alertDialogModel: RegisterAlertDialogModel,
) : BaseDialog<ModalChooseGalleryCameraBinding>(ModalChooseGalleryCameraBinding::bind, R.layout.modal_choose_gallery_camera) {
    override fun initListener() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}