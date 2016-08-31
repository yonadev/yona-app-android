/*
 * Copyright (c) 2016 Stichting Yona Foundation
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nu.yona.app.ui.comment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import nu.yona.app.R;
import nu.yona.app.customview.YonaFontTextView;

/**
 * Created by bhargavsuthar on 28/07/16.
 */
public class CommentHolder extends RecyclerView.ViewHolder {

    private View view;
    private View lineTop;
    private View parentCommentLayout;
    private View childCommentLayout;
    private ImageView imgParentBuddyIcon;
    private ImageView imgChildBuddyIcon;
    private YonaFontTextView txtParentBuddyName;
    private YonaFontTextView txtChildBuddyName;
    private YonaFontTextView txtParentBuddyMsg;
    private YonaFontTextView txtChildBuddyMsg;
    private YonaFontTextView txtParentCommentReplay;
    private YonaFontTextView txtChildCommentReplay;
    private YonaFontTextView profileImageTxt;
    private View.OnClickListener clickListener;

    public CommentHolder(View itemView, View.OnClickListener listener, int id) {
        super(itemView);
        this.view = itemView;
        this.clickListener = listener;
        switch (id) {
            case 1:
                initParentCommentControl();
                break;
            case 2:
                initChildCommentControl();
                break;
            default:
                break;
        }

    }

    private void initParentCommentControl() {
        //view.findViewById(R.id.childCommentLayout).setVisibility(View.GONE);
        view.findViewById(R.id.parentCommentLayout).setVisibility(View.VISIBLE);
        parentCommentLayout = view.findViewById(R.id.parentCommentLayout);
        lineTop = view.findViewById(R.id.horizontalline);
        imgParentBuddyIcon = (ImageView) view.findViewById(R.id.imgParentBuddyIcon);
        profileImageTxt = (YonaFontTextView) view.findViewById(R.id.profileTextIcon);
        txtParentBuddyName = (YonaFontTextView) view.findViewById(R.id.txtBuddyName);
        txtParentBuddyMsg = (YonaFontTextView) view.findViewById(R.id.txtBuddyMessage);
        txtParentCommentReplay = (YonaFontTextView) view.findViewById(R.id.txtBuddyReply);
        txtParentCommentReplay.setOnClickListener(clickListener);
    }

    private void initChildCommentControl() {
        //view.findViewById(R.id.parentCommentLayout).setVisibility(View.GONE);
        view.findViewById(R.id.childCommentLayout).setVisibility(View.VISIBLE);
        childCommentLayout = view.findViewById(R.id.childCommentLayout);
        imgChildBuddyIcon = (ImageView) view.findViewById(R.id.imgChildBuddyIcon);
        profileImageTxt = (YonaFontTextView) view.findViewById(R.id.profileTextIcon);
        txtChildBuddyName = (YonaFontTextView) view.findViewById(R.id.childBuddyname);
        txtChildBuddyMsg = (YonaFontTextView) view.findViewById(R.id.childBuddyMessage);
        txtChildCommentReplay = (YonaFontTextView) view.findViewById(R.id.txtChildBuddyReply);
        txtChildCommentReplay.setOnClickListener(clickListener);
    }


    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getParentCommentLayout() {
        return parentCommentLayout;
    }

    public View getChildCommentLayout() {
        return childCommentLayout;
    }

    public ImageView getImgParentBuddyIcon() {
        return imgParentBuddyIcon;
    }

    public ImageView getImgChildBuddyIcon() {
        return imgChildBuddyIcon;
    }

    public YonaFontTextView getTxtParentBuddyName() {
        return txtParentBuddyName;
    }

    public YonaFontTextView getTxtChildBuddyName() {
        return txtChildBuddyName;
    }

    public YonaFontTextView getTxtParentBuddyMsg() {
        return txtParentBuddyMsg;
    }

    public YonaFontTextView getTxtChildBuddyMsg() {
        return txtChildBuddyMsg;
    }

    public YonaFontTextView getTxtParentCommentReplay() {
        return txtParentCommentReplay;
    }

    public void setTxtParentCommentReplay(YonaFontTextView txtParentCommentReplay) {
        this.txtParentCommentReplay = txtParentCommentReplay;
    }

    public YonaFontTextView getTxtChildCommentReplay() {
        return txtChildCommentReplay;
    }

    public void setTxtChildCommentReplay(YonaFontTextView txtChildCommentReplay) {
        this.txtChildCommentReplay = txtChildCommentReplay;
    }

    public View getLineTop() {
        return lineTop;
    }

    public void setLineTop(View lineTop) {
        this.lineTop = lineTop;
    }

    public YonaFontTextView getProfileImageTxt() {
        return this.profileImageTxt;
    }

    public void setProfileImageTxt(YonaFontTextView profileImageTxt) {
        this.profileImageTxt = profileImageTxt;
    }
}
