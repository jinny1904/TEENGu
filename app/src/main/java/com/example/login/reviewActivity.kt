package com.example.login
// 홈 -> 산부인과 -> 지도 마커 클릭 -> 후기 클릭(산부인과 후기)
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*

class reviewActivity : AppCompatActivity() {

    lateinit var username : TextView
    lateinit var reviewText : EditText
    lateinit var regButton : Button
    lateinit var review : LinearLayout
    lateinit var login_id : String

    // db 객체 선언 및 연결
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review2)

        // 뒤로가기 버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbManager = DBManager(this, "map", null, 1)

        username = findViewById(R.id.username)
        reviewText = findViewById(R.id.reviewText)
        regButton = findViewById(R.id.regButton)
        review = findViewById(R.id.review)

        // 병원 이름 받아오기
        val hosName : String? = intent?.getStringExtra("hosName").toString()
        // id 받아오기
        val id = intent?.getStringExtra("intent_userid")
        login_id = id.toString()

        username.text = "$id"

        // 후기 리스트 출력
        sqlitedb = dbManager.readableDatabase

        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM review where hosName ='"+hosName+"';", null)

        var num : Int=0
        while(cursor.moveToNext()){

            if(cursor == null)
                Toast.makeText(applicationContext, "리뷰가 없습니다.", Toast.LENGTH_SHORT).show()

            var str_username = cursor.getString(cursor.getColumnIndex("username")).toString()
            var str_userRev = cursor.getString(cursor.getColumnIndex("userRev")).toString()

            var layout_items:LinearLayout = LinearLayout(this)
            layout_items.orientation = LinearLayout.VERTICAL
            layout_items.setPadding(50,30,50,30)
            layout_items.id = num
            layout_items.setBackgroundColor(Color.parseColor("#DCDCDC"))
            layout_items.setTag(str_username)

            var tvName: TextView = TextView(this)
            tvName.text = str_username
            tvName.textSize = 17F
            tvName.setTextColor(Color.BLACK)
            layout_items.addView(tvName)

            var tvreview: TextView = TextView(this)
            tvreview.text = str_userRev
            tvreview.textSize = 15F
            layout_items.addView(tvreview)

            review.addView(layout_items)
            num++
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 후기 작성
        regButton.setOnClickListener {
            var str_review = reviewText?.text.toString()

            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("INSERT INTO review VALUES('$id', '$hosName', '$str_review');")
            Toast.makeText(applicationContext, "후기가 작성되었습니다.", Toast.LENGTH_SHORT).show()
            sqlitedb.close()

            val intent = Intent(this, reviewActivity::class.java)
            intent.putExtra("intent_userid",id)
            intent.putExtra("hosName",hosName)
            startActivity(intent)
        }
    }

    //홈화면에 쓰일 메뉴 리소스 지정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    //우측 상단 홈 메뉴 누를 시 HomeActivity로 이동
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.action_home ->{
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("intent_userid",login_id)
                startActivity(intent)

                return true
            }
            //우측 상단 채팅 메뉴 누를시 ChatActivity로 이동
            R.id.action_chat ->{
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("intent_userid", login_id)
                startActivity(intent)

                return true
            }

            //마이페이지로 이동
            R.id.action_mypage -> {
                val intent = Intent(this, MypageActivity::class.java)
                intent.putExtra("intent_userid", login_id)
                startActivity(intent)

                return true
            }
            // 이전 페이지로 이동
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}