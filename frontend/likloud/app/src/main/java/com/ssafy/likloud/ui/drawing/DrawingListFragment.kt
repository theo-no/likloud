package com.ssafy.likloud.ui.drawing

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager
import com.ssafy.likloud.ApplicationClass
import com.ssafy.likloud.MainActivity
import com.ssafy.likloud.MainActivityViewModel
import com.ssafy.likloud.R
import com.ssafy.likloud.base.BaseFragment
import com.ssafy.likloud.data.model.CommentDto
import com.ssafy.likloud.data.model.DrawingDetailDto
import com.ssafy.likloud.data.model.DrawingListDto
import com.ssafy.likloud.data.model.MemberProfileDto
import com.ssafy.likloud.databinding.FragmentDrawingListBinding
import dagger.hilt.android.AndroidEntryPoint
<<<<<<< frontend/likloud/app/src/main/java/com/ssafy/likloud/ui/drawing/DrawingListFragment.kt
import kotlinx.coroutines.delay
=======
import kotlinx.coroutines.flow.collectLatest
>>>>>>> frontend/likloud/app/src/main/java/com/ssafy/likloud/ui/drawing/DrawingListFragment.kt
import kotlinx.coroutines.launch

private const val TAG = "차선호"
@AndroidEntryPoint
class DrawingListFragment : BaseFragment<FragmentDrawingListBinding>(FragmentDrawingListBinding::bind, R.layout.fragment_drawing_list) {

    private val drawingListFragmentViewModel : DrawingListFragmentViewModel by viewModels()
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var drawingListAdapter: DrawingListAdapter
    private lateinit var commentListAdapter: CommentListAdapter
    private var isScrolling = false


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
        init()
        initListener()
        initObserver()
    }

    private fun init(){
        drawingListFragmentViewModel.getRecentOrderDrawingListDtoList()
        toggleButton(binding.buttonRecentOrder)
        initRecyclerView()
        initCommentRecyclerView()

        binding.layoutInfo.translationX = 1600f
        binding.layoutComment.translationX = 1600f
        binding.recyclerviewDrawaing.translationX = -1600f
        initAnimation()
    }

    private fun initAnimation() {
        lifecycleScope.launch {
            makeAnimationX(binding.layoutInfo, 0f, 450)
            delay(100)
            makeAnimationX(binding.layoutComment, 0f, 500)
            delay(50)
            makeAnimationX(binding.recyclerviewDrawaing, 0f, 600)
        }
    }

    override fun initListener(){

        binding.apply {
            //최신순 눌렀을 때
            buttonRecentOrder.setOnClickListener {
                if(!isScrolling) {
                    drawingListFragmentViewModel.getRecentOrderDrawingListDtoList()
                    initRecyclerView()
                    toggleButton(buttonRecentOrder)
                }
            }
            //랭킹순 눌렀을 때
            buttonRankingOrder.setOnClickListener{
                if(!isScrolling) {
                    drawingListFragmentViewModel.getRankingOrderDrawingListDtoList()
                    initRecyclerView()
                    toggleButton(buttonRankingOrder)
                }
            }
            //조회순 눌렀을 때
            buttonViewOrder.setOnClickListener {
                if(!isScrolling) {
                    drawingListFragmentViewModel.getViewOrderDrawingListDtoLit()
                    initRecyclerView()
                    toggleButton(buttonViewOrder)
                }
            }
            //좋아요 눌렀을 때
            imageHeart.setOnClickListener {
                drawingListFragmentViewModel.changeLikeCount()
                drawingListFragmentViewModel.changeIsLiked()
            }
            //뒤로가기 눌렀을 때
            buttonBack.setOnClickListener {
                findNavController().popBackStack()
            }
            //댓글 입력 눌렀을 때
            buttonDrawingComment.setOnClickListener {
                val content = edittextDrawingComment.text.toString()
                if(content == ""){
                    Toast.makeText(mainActivity,"댓글을 입력하세요",Toast.LENGTH_SHORT).show()
                }else{
                    //댓글 입력 함수
                    drawingListFragmentViewModel.registDrawingComment(drawingListFragmentViewModel.currentDrawingDetailDto.value!!.drawingId, content)
                    edittextDrawingComment.setText("")
                    edittextDrawingComment.clearFocus()
                    val keyboard = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    keyboard.hideSoftInputFromWindow(edittextDrawingComment.windowToken,0)
                }
            }
            buttonReport.setOnClickListener {
                drawingListFragmentViewModel.setDrawingReportDialog()
                drawingListFragmentViewModel.drawingReportDialog.show(childFragmentManager, "report")
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

    private fun initObserver(){

        drawingListFragmentViewModel.currentDrawingListDtoList.observe(viewLifecycleOwner){
            if(it.size!=0) {
                drawingListAdapter.submitList(it)
                drawingListFragmentViewModel.getCurrentDrawingDetailDto(it[0])
            }
        }

        drawingListFragmentViewModel.currentDrawingDetailDto.observe(viewLifecycleOwner){
            //현재 가운데 있는 그림 정보 조회 & 초기 좋아요 세팅
            drawingListFragmentViewModel.getCurrentDrawingMember(it.memberId)
            drawingListFragmentViewModel.setIsLiked()
            drawingListFragmentViewModel.setLikeCount()
        }

        drawingListFragmentViewModel.currentDrawingMember.observe(viewLifecycleOwner){
            //현재 그림에 대한 정보, 그림 그린 멤버 정보 뷰 세팅
            initInfoView(drawingListFragmentViewModel.currentDrawingDetailDto.value!!, it)
            isScrolling = false
        }

        drawingListFragmentViewModel.isLiked.observe(viewLifecycleOwner){
            if(it){
                binding.imageHeart.setImageResource(R.drawable.icon_selected_heart)
            }else{
                binding.imageHeart.setImageResource(R.drawable.icon_unselected_heart)
            }
        }

        drawingListFragmentViewModel.likeCount.observe(viewLifecycleOwner){
            binding.textLikeCount.text = it.toString()
        }

        drawingListFragmentViewModel.currentDrawingCommentList.observe(viewLifecycleOwner){
            Log.d(TAG, "commentObserver .... $it")
            commentListAdapter.submitList(it.toMutableList())
            if(it.size!=0) binding.recyclerviewDrawingComment.smoothScrollToPosition(it.size)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            drawingListFragmentViewModel.isReported.collectLatest {
                Toast.makeText(mainActivity, "신고 완료", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initInfoView(drawingDetail: DrawingDetailDto, member: MemberProfileDto){
        binding.apply {
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
        }
    }

    private fun toggleButton(view: View){
        binding.apply {
            buttonRecentOrder.background = ContextCompat.getDrawable(mainActivity, R.drawable.button_frame_black)
            buttonRankingOrder.background = ContextCompat.getDrawable(mainActivity, R.drawable.button_frame_black)
            buttonViewOrder.background = ContextCompat.getDrawable(mainActivity, R.drawable.button_frame_black)
        }
        view.background = ContextCompat.getDrawable(mainActivity, R.drawable.button_frame_skyblue)
    }

    private fun initRecyclerView(){
        drawingListAdapter = DrawingListAdapter(activityViewModel)
        binding.recyclerviewDrawaing.apply {
            this.adapter = drawingListAdapter.apply {
                itemClickListner = object: DrawingListAdapter.ItemClickListener{
                    override fun onClick(drawing: DrawingListDto) {
                        if(activityViewModel.memberInfo.value!!.silverCoin>=5) {
                            drawingListFragmentViewModel.registNft(drawing.drawingId)
                        }else{
                            //여기서 silverCoin 부족하다고 뜸
                            Toast.makeText(mainActivity,"silverCoin 확인 바람", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
            set3DItem(true)
            setAlpha(true)
            setOrientation(RecyclerView.VERTICAL)
            setItemSelectListener(object : CarouselLayoutManager.OnSelected {
                //본인한테서 멈췄을 때 이벤트
                override fun onItemSelected(position: Int) {
                    //여기서 selectedDrawing을 가지고 DrawingDetailDto 받아라
                    drawingListFragmentViewModel.getCurrentDrawingDetailDto(drawingListFragmentViewModel.currentDrawingListDtoList.value!![position])
                }
            })
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                        isScrolling = true
                    }
                }
            })
        }
    }

    private fun initCommentRecyclerView(){
        Log.d(TAG, "commentList : ${drawingListFragmentViewModel.currentDrawingCommentList.value} ")
        commentListAdapter = CommentListAdapter(activityViewModel)
        binding.recyclerviewDrawingComment.apply {
            this.adapter = commentListAdapter.apply {
                this.itemClickListner = object: CommentListAdapter.ItemClickListener{
                    override fun onClick(comment: CommentDto, position: Int) {
                        drawingListFragmentViewModel.deleteDrawingComment(comment.commentId, position)
                    }
                }
            }
            layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    /**
     * 뷰에 X축으로 움직이는 애니메이션을 적용시킵니다.
     */
    private fun makeAnimationX(view: View, values: Float, speed: Long) {
        ObjectAnimator.ofFloat(view, "translationX", values).apply {
//            interpolator = DecelerateInterpolator()
            interpolator = OvershootInterpolator()
//            interpolator = AccelerateInterpolator()
//            interpolator = AccelerateDecelerateInterpolator()
            duration = speed
            start()
        }
    }

    fun sendReport(content: String){
        drawingListFragmentViewModel.sendReport(content)
    }
}