package com.xmx.tango.Services;

import com.xmx.tango.MainActivity;
import com.xmx.tango.Tools.OperationLog.OperationLogEntityManager;
import com.xmx.tango.Tools.ServiceBase.BaseService;
import com.xmx.tango.Tools.Timer;

public class MainService extends BaseService {

    long time = System.currentTimeMillis();
    Timer timer;

    @Override
    protected void processLogic() {
        timer = new Timer() {
            @Override
            public void timer() {
                long now = System.currentTimeMillis();
                showToast("服务已运行" + (now - time) + "毫秒");
                OperationLogEntityManager.getInstance().addLog("服务已运行" + (now - time) + "毫秒");
            }
        };
        timer.start(5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OperationLogEntityManager.getInstance().addLog("服务停止");

        timer.stop();
    }

    @Override
    protected void setForeground() {
        showForeground(MainActivity.class, "正在运行");
    }
}
