package com.deepwaterooo.sdk.utils;

import com.deepwaterooo.sdk.beans.PlayerDO;

public interface LoginListener {
    /**
     * This method is to get called when user login successful
     * 用户注册或是登录成功了之后,用于游戏端的监听实现,准备开始游戏 ?
     */
    public void didFinishSdkUserConfiguration();

    /**
     * This method is get called when player selectedin select a player screen.
     * 这个晚点儿再处理好了
     * @param playerDO
     */
     public void didSelectedChild(PlayerDO playerDO);
}
