package com.liuhaoyuan.myplayer.fragment;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liuhaoyuan.myplayer.APP;
import com.liuhaoyuan.myplayer.R;
import com.liuhaoyuan.myplayer.utils.ConstantValues;

/**
 * Created by hyliu on 2017/2/1.
 */

public class ThemeFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_theme, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.lv_theme);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        ThemeAdapter adapter = new ThemeAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private class ThemeAdapter extends RecyclerView.Adapter<ThemeViewHolder> {
        private int[] colors = new int[]{R.color.purplePrimary, R.color.indigoPrimary, R.color.pinkPrimary,
                R.color.cyanPrimary, R.color.redPrimary, R.color.deepPurplePrimary,
                R.color.greenPrimary, R.color.tealPrimary, R.color.orangePrimary,
                R.color.yellowPrimary, R.color.brownPrimary, R.color.greyPrimary,
                R.color.bluePrimary, R.color.lightBluePrimary, R.color.lightGreenPrimary,
                R.color.limePrimary, R.color.amberPrimary, R.color.blueGreyPrimary
        };
        private int[] themes = new int[]{R.style.AppTheme_NoActionBar, R.style.IndigoTheme, R.style.PinkTheme,
                R.style.CyanTheme, R.style.RedTheme, R.style.DeepPurpleTheme,
                R.style.GreenTheme, R.style.TealTheme, R.style.OrangeTheme,
                R.style.YellowTheme, R.style.BrownTheme, R.style.GreyTheme,
                R.style.BlueTheme, R.style.LightBlueTheme, R.style.LightGreenTheme,
                R.style.LimeTheme, R.style.AmberTheme, R.style.BlueGreyTheme
        };
        private String[] colorNames = new String[]{"Purple", "Indigo", "Pink", "Cyan", "Red",
                "Deep Purple", "Green", "Teal", "Orange", "Yellow", "Brown", "Grey",
                "Blue", "LightBlue", "LightGreen", "Lime", "Amber", "BlueGrey"};

        @Override
        public ThemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme, parent, false);
            ThemeViewHolder holder = new ThemeViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ThemeViewHolder holder, final int position) {
            int color = getResources().getColor(colors[position]);
            holder.relativeLayout.setBackgroundColor(color);
            holder.textView.setText(colorNames[position]);
            final APP application = (APP) getActivity().getApplication();
            if (themes[position] == application.getCurrentTheme()) {
                holder.imageView.setVisibility(View.VISIBLE);
            } else {
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (themes[position] != application.getCurrentTheme()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("确定更换主题？");
                        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                application.setCurrentTheme(themes[position]);
                                Intent intent = new Intent();
                                intent.setAction(ConstantValues.ACTION_CHANGE_THEME);
                                getContext().sendBroadcast(intent);
                                notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return colors.length;
        }
    }

    private class ThemeViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private TextView textView;
        private ImageView imageView;

        public ThemeViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_theme);
            textView = (TextView) itemView.findViewById(R.id.tv_theme);
            imageView = (ImageView) itemView.findViewById(R.id.iv_checked);
        }
    }
}
