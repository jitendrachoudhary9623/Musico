package com.musico.jitcodez.musico.Adapters

import android.content.Context
import android.provider.Settings
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.musico.jitcodez.musico.Activities.MainActivity
import com.musico.jitcodez.musico.Fragments.AboutUsFragment
import com.musico.jitcodez.musico.Fragments.FavoriteFragment
import com.musico.jitcodez.musico.Fragments.MainScreenFragment
import com.musico.jitcodez.musico.Fragments.SettingsFragment
import com.musico.jitcodez.musico.R

/**
 * Created by jitu on 24/1/18.
 */
class NavigationDrawer_Adapter(textData:ArrayList<String>,imageData:IntArray,context: Context): RecyclerView.Adapter<NavigationDrawer_Adapter.Item_ViewHolder>()
{
    var mtextData:ArrayList<String>?=null;
    var mImageData:IntArray?=null;
    var mcontext:Context?=null;
    init{
        this.mtextData=textData;
        this.mImageData=imageData;
        this.mcontext=context;

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Item_ViewHolder {
       val itemView= LayoutInflater.from(parent?.context)
                .inflate(R.layout.recycler_nav_bar,parent,false);
        var returnThis=Item_ViewHolder(itemView);
        return returnThis;
    }

    override fun getItemCount(): Int {

        return 4;
    }

    override fun onBindViewHolder(holder: Item_ViewHolder?, position: Int) {
holder?.Icon_GET?.setBackgroundColor(mImageData?.get(position) as Int);
holder?.Text_GET?.setText(mtextData?.get(position));
        holder?.holderLayout?.setOnClickListener(
                {
                   when(position)
                   {
                       0->{
                            val mainScreenFragment=MainScreenFragment();
                           (mcontext as MainActivity).supportFragmentManager
                                   .beginTransaction()
                                   .replace(R.id.details_fragment,mainScreenFragment)
                                   .commit()
                       }
                       1->{
                           val favoritesFragment=FavoriteFragment()
                           (mcontext as MainActivity).supportFragmentManager
                                   .beginTransaction()
                                   .replace(R.id.details_fragment,favoritesFragment)
                                   .commit()
                       }
                       2->{
                           val settingFragment= SettingsFragment();
                           (mcontext as MainActivity).supportFragmentManager
                                   .beginTransaction()
                                   .replace(R.id.details_fragment,settingFragment)
                                   .commit()
                       }
                       3->{
                           val aboutFragment=AboutUsFragment()
                           (mcontext as MainActivity).supportFragmentManager
                                   .beginTransaction()
                                   .replace(R.id.details_fragment,aboutFragment)
                                   .commit()
                       }
                   }
                    MainActivity.statified.drawerLayout?.closeDrawers()
                }
        )
    }

    class Item_ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)
    {
        var Icon_GET: ImageView?=null;
        var Text_GET: TextView?=null;
        var holderLayout: RelativeLayout?=null;

        init
        {
        Icon_GET=itemView?.findViewById(R.id.icon_navbar);
            Text_GET=itemView?.findViewById(R.id.text_navbar);
            holderLayout=itemView?.findViewById(R.id.layout_holder)

        }
    }
}