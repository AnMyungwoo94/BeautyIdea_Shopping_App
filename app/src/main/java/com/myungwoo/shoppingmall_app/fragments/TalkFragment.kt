package com.myungwoo.shoppingmall_app.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.myungwoo.shoppingmall_app.R
import com.myungwoo.shoppingmall_app.databinding.FragmentTalkBinding
import com.myungwoo.shoppingmall_app.board.BoardInsideActivity
import com.myungwoo.shoppingmall_app.board.BoardListRvAdapter
import com.myungwoo.shoppingmall_app.board.BoardModel
import com.myungwoo.shoppingmall_app.board.BoardWriteActivity
import com.myungwoo.shoppingmall_app.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TalkFragment : Fragment() {

    private lateinit var binding : FragmentTalkBinding
    private lateinit var boardRVAdapter :BoardListRvAdapter
    private val TAG = TalkFragment::class.java.simpleName
    private val boardDataList = mutableListOf<BoardModel>()
    private val boardKeyList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_talk, container, false)

        boardRVAdapter = BoardListRvAdapter(boardDataList)
        binding.boardListView.adapter = boardRVAdapter

        binding.boardListView.setOnItemClickListener { parent, view, position, id ->
        //게시글보기로 값 넘겨주기
        //첫번째 방법으로는 listview에 있는 데이터 title, content, time의 값을 intent로 넘겨주기
//            val intent = Intent(context, BoardInsideActivity::class.java)
//            intent.putExtra("title", boardDataList[position].title)
//            intent.putExtra("content", boardDataList[position].content)
//            intent.putExtra("time", boardDataList[position].time)
//            startActivity(intent)

            //두번째 방법으로는 Firebase에 있는 board에 대한 데이터의 id를 기반으로 가져오기
            val intent = Intent(context, BoardInsideActivity::class.java)
            intent.putExtra("key", boardKeyList[position])
            startActivity(intent)
        }


       binding.writeBtn.setOnClickListener {
            val intent = Intent(context, BoardWriteActivity::class.java)
            startActivity(intent)

        }
        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_homeFragment2)
        }

        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_tipFragment)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_bookmarkFragment)
        }

        binding.storeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_talkFragment_to_shopFragment)
        }
        getFBBoardData()
        return binding.root
    }
    private fun getFBBoardData(){
        val postListener = object : ValueEventListener {
            //Content 데이터읽기
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                boardDataList.clear()
                for (dataModel in dataSnapshot.children) {
                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(BoardModel::class.java)
                    boardDataList.add(item!!)
                    boardKeyList.add(dataModel.key.toString())
                }
                //reverse()로 최신글이 먼저 나오게 하기
                boardKeyList.reverse()
                boardDataList.reverse()
                boardRVAdapter.notifyDataSetChanged()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.addValueEventListener(postListener)
    }

}