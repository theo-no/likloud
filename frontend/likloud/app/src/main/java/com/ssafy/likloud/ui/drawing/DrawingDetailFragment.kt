package com.ssafy.likloud.ui.drawing

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.likloud.MainActivity
import com.ssafy.likloud.MainActivityViewModel
import com.ssafy.likloud.R
import com.ssafy.likloud.base.BaseFragment
import com.ssafy.likloud.data.model.CommentDto
import com.ssafy.likloud.data.model.DrawingDetailDto
import com.ssafy.likloud.data.model.MemberProfileDto
import com.ssafy.likloud.databinding.FragmentDrawingDetailBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "차선호"
@AndroidEntryPoint
class DrawingDetailFragment : BaseFragment<FragmentDrawingDetailBinding>(
    FragmentDrawingDetailBinding::bind, R.layout.fragment_drawing_detail
) {

    private val drawingDetailFragmentViewModel : DrawingDetailFragmentViewModel by viewModels()
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    val args: DrawingDetailFragmentArgs by navArgs()
    private lateinit var commentListAdapter: CommentListAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        init()
        initListener()
    }

    private fun initObserver(){
        drawingDetailFragmentViewModel.currentDrawingDetail.observe(viewLifecycleOwner){
            //현재 가운데 있는 그림 정보 조회 & 초기 좋아요 세팅
            drawingDetailFragmentViewModel.getCurrentDrawingMember(it.memberId)
            drawingDetailFragmentViewModel.setIsLiked()
            drawingDetailFragmentViewModel.setLikeCount()
        }

        drawingDetailFragmentViewModel.currentDrawingMember.observe(viewLifecycleOwner) {
            initInfoView(drawingDetailFragmentViewModel.currentDrawingDetail.value!!, it)
        }

        drawingDetailFragmentViewModel.isLiked.observe(viewLifecycleOwner){
            Log.d(TAG, "current isLiked: $it")
            if(it){
                binding.imageHeart.setImageResource(R.drawable.icon_selected_heart)
            }else{
                binding.imageHeart.setImageResource(R.drawable.icon_unselected_heart)
            }
        }

        drawingDetailFragmentViewModel.likeCount.observe(viewLifecycleOwner){
            binding.textLikeCount.text = it.toString()
        }

        drawingDetailFragmentViewModel.currentDrawingCommentList.observe(viewLifecycleOwner){
            commentListAdapter.submitList(it.toMutableList())

        }

        drawingDetailFragmentViewModel.nftYn.observe(viewLifecycleOwner){
            if(it){
                binding.buttonNft.setBackgroundResource(R.drawable.frame_rounded_border_skyblue_radius50)
            }else{
                binding.buttonNft.setBackgroundResource(R.drawable.frame_button_rounded_border_grey_radius50)
            }
        }

        drawingDetailFragmentViewModel.isSuccess.observe(viewLifecycleOwner){
            if(it){
//                activityViewModel.
            }
        }
    }

    private fun init(){
        //여기서 args.drawingId로 DrawingDetailDto 불러와야 함
        drawingDetailFragmentViewModel.getCurrentPhotoDrawingDetail(args.drawingId)
        initCommentRecyclerView()
    }

    override fun initListener() {
        binding.apply {
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            imageHeart.setOnClickListener {
                drawingDetailFragmentViewModel.changeLikeCount()
                drawingDetailFragmentViewModel.changeIsLiked()
            }
            buttonDrawingComment.setOnClickListener {
                val content = edittextDrawingComment.text.toString()
                if(content == ""){
                    Toast.makeText(mainActivity,"댓글을 입력하세요", Toast.LENGTH_SHORT).show()
                }else{
                    //댓글 입력 함수
                    drawingDetailFragmentViewModel.registDrawingComment(drawingDetailFragmentViewModel.currentDrawingDetail.value!!.drawingId, content)
                    edittextDrawingComment.setText("")
                    edittextDrawingComment.clearFocus()
                    val keyboard = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    keyboard.hideSoftInputFromWindow(edittextDrawingComment.windowToken,0)
                }
            }
            buttonNft.setOnClickListener {
                if(activityViewModel.memberInfo.value!!.wallet == null){
                    // 지갑 발행해라
                }else{
                    if(activityViewModel.memberInfo.value!!.silverCoin>=5) {
                        drawingDetailFragmentViewModel.registNft(args.drawingId)
                    }else{
                        //여기서 siverCoin 부족 메시지
                        Toast.makeText(mainActivity,"silverCoin 확인 바람", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        // 안드로이드 뒤로가기 버튼 눌렀을 때
        mainActivity.onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun initInfoView(drawingDetail: DrawingDetailDto,member: MemberProfileDto){
        binding.apply {
            Glide.with(binding.imageCurrentDrawing)
                .load(drawingDetail.imageUrl)
                .into(binding.imageCurrentDrawing)
            Glide.with(binding.imageProfileColor)
                .load(activityViewModel.waterDropColorList[member.profileColor].resourceId)
                .into(binding.imageProfileColor)
            Glide.with(binding.imageProfileFace)
                .load(activityViewModel.waterDropFaceList[member.profileFace].resourceId)
                .into(binding.imageProfileFace)
            Glide.with(binding.imageProfileAccessory)
                .load(activityViewModel.waterDropAccessoryList[member.profileAccessory].resourceId)
                .into(binding.imageProfileAccessory)
            textDrawingNickname.text = member.nickname
            textDrawingTitle.text = drawingDetail.title
            textDrawingContent.text = drawingDetail.content
            textLikeCount.text = drawingDetail.likesCount.toString()
            textViewCount.text = drawingDetail.viewCount.toString()
            Log.d(TAG, "member ${member.nickname} // user ${activityViewModel.memberInfo.value!!.nickname}")
            if(member.nickname == activityViewModel.memberInfo.value!!.nickname){
                buttonNft.visibility = View.VISIBLE
            }else{
                buttonNft.visibility = View.GONE
            }
        }
    }

    private fun initCommentRecyclerView(){
        Log.d(TAG, "commentList : ${drawingDetailFragmentViewModel.currentDrawingCommentList.value} ")
        commentListAdapter = CommentListAdapter(activityViewModel)
        binding.recyclerviewDrawingComment.apply {
            this.adapter = commentListAdapter.apply {
                this.itemClickListner = object: CommentListAdapter.ItemClickListener{
                    override fun onClick(comment: CommentDto, position: Int) {
                        drawingDetailFragmentViewModel.deleteDrawingComment(comment.commentId, position)
                    }
                }
            }
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

}