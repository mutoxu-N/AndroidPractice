package com.github.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.SimpleAdapter

class MenuListFragment : Fragment() {

    private var _isLayoutXLarge = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val menuThanksFrame = activity?.findViewById<FrameLayout>(R.id.menuThanksFrame)
        if(menuThanksFrame == null)
            _isLayoutXLarge = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_menu_list, container, false)
        val lvMenu = view.findViewById<ListView>(R.id.lvMenu)
        val menuList: MutableList<MutableMap<String, String>> = mutableListOf()

        menuList.add(mutableMapOf("name" to "からあげ定食", "price" to "800円"))
        menuList.add(mutableMapOf("name" to "ハンバーグ定食", "price" to "850円"))
        menuList.add(mutableMapOf("name" to "生姜焼き定食", "price" to "850円"))
        menuList.add(mutableMapOf("name" to "ステーキ定食", "price" to "1000円"))
        menuList.add(mutableMapOf("name" to "野菜炒め定食", "price" to "750円"))
        menuList.add(mutableMapOf("name" to "とんかつ定食", "price" to "900円"))
        menuList.add(mutableMapOf("name" to "ミンチかつ定食", "price" to "850円"))
        menuList.add(mutableMapOf("name" to "チキンカツ定食", "price" to "900円"))
        menuList.add(mutableMapOf("name" to "コロッケ定食", "price" to "850円"))
        menuList.add(mutableMapOf("name" to "回鍋肉定食", "price" to "750円"))
        menuList.add(mutableMapOf("name" to "麻婆豆腐定食", "price" to "800円"))

        val from = arrayOf("name", "price")
        val to = intArrayOf(android.R.id.text1, android.R.id.text2)
        val adapter = SimpleAdapter(activity, menuList, android.R.layout.simple_list_item_2, from, to)
        lvMenu.adapter = adapter

        lvMenu.onItemClickListener = ListItemClickListener()

        return view
    }

    private inner class ListItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            val item = parent.getItemAtPosition(pos) as MutableMap<String, String>
            val menuName = item["name"]
            val menuPrice = item["price"]

            val bundle = Bundle()
            bundle.putString("menuName", menuName)
            bundle.putString("menuPrice", menuPrice)

            if(_isLayoutXLarge) {
                val transaction = fragmentManager?.beginTransaction()
                val menuThanksFragment = MenuThanksFragment()
                menuThanksFragment.arguments = bundle
                transaction?.replace(R.id.menuThanksFrame, menuThanksFragment)
                transaction?.commit()

            } else {
                val intent2menuThanks = Intent(activity, MenuThanksActivity::class.java)
                intent2menuThanks.putExtras(bundle)
                startActivity(intent2menuThanks)
            }

        }

    }

}