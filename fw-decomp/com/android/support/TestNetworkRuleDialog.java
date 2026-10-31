package com.android.support;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Process;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.support.TestNetworkService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
final class TestNetworkRuleDialog {
    private static final AtomicBoolean RUNNING = new AtomicBoolean();
    private final Activity activity;
    private volatile File cancelFile;
    private volatile TestNetworkRule pendingRule;
    private final SharedPreferences recovery;
    private final RootRunner runner;

    interface RootRunner {
        Result run(String str, long j);
    }

    void show(String str) {
    }

    static final class Result {
        final String output;
        final boolean success;

        Result(boolean z, String str) {
            this.success = z;
            this.output = str;
        }
    }

    TestNetworkRuleDialog(Activity activity, RootRunner rootRunner) {
        this.activity = activity;
        this.runner = rootRunner;
        this.recovery = activity.getApplicationContext().getSharedPreferences("test_network_recovery", 0);
    }

    private /* synthetic */ void lambda$show$0(AlertDialog alertDialog, TextView textView, EditText editText, EditText editText2, EditText editText3, Button button, View view) {
        chooseService(alertDialog, textView, editText, editText2, editText3, button);
    }

    private /* synthetic */ void lambda$show$1(DialogInterface dialogInterface) throws IOException {
        requestStop();
    }

    private /* synthetic */ void lambda$show$2(TextView textView, Button button, Button button2, View view) {
        if (!requestStop()) {
            textView.setText("取消标记写入失败，请等待本次实验自动结束。");
        } else if (RUNNING.get()) {
            textView.setText("正在恢复网络…");
        } else {
            retryCleanup(textView, button, button2);
        }
    }

    private /* synthetic */ void lambda$show$5(final TextView textView, String str, EditText editText, EditText editText2, EditText editText3, final Button button, final Button button2, View view) {
        AtomicBoolean atomicBoolean = RUNNING;
        if (atomicBoolean.get()) {
            textView.setText("已有实验正在运行，请等待恢复完成。");
            return;
        }
        if (this.pendingRule != null) {
            textView.setText("请先恢复上次实验。");
            return;
        }
        try {
            int i = this.activity.getPackageManager().getApplicationInfo(str, 0).uid;
            String[] packagesForUid = this.activity.getPackageManager().getPackagesForUid(i);
            if (packagesForUid == null || packagesForUid.length != 1 || !str.equals(packagesForUid[0])) {
                throw new IllegalArgumentException("游戏使用共享 UID，本次实验未启动");
            }
            final TestNetworkRule testNetworkRule = new TestNetworkRule(false, Process.myUid(), i, editText.getText().toString(), editText2.getText().toString(), editText3.getText().toString().trim(), UUID.randomUUID().toString().replace("-", "").substring(0, 12));
            if (atomicBoolean.compareAndSet(false, true)) {
                final File file = new File(this.activity.getCacheDir(), "network-" + testNetworkRule.chain + ".cancel");
                if (!this.recovery.edit().putString("nonce", testNetworkRule.chain.substring(testNetworkRule.chain.length() - 12)).putInt("uid", testNetworkRule.targetUid).putString("address", testNetworkRule.address).putString("port", Integer.toString(testNetworkRule.port)).putString("protocol", testNetworkRule.protocol).commit()) {
                    atomicBoolean.set(false);
                    textView.setText("实验恢复记录保存失败，本次未启动。");
                    return;
                }
                this.pendingRule = testNetworkRule;
                this.cancelFile = file;
                button.setEnabled(false);
                button2.setEnabled(true);
                textView.setText("正在执行：" + testNetworkRule.address + ":" + testNetworkRule.port + " / " + testNetworkRule.protocol.toUpperCase(Locale.ROOT) + "\n30 秒内自动恢复；关闭此窗口也会请求恢复。");
                new Thread(new Runnable() { // from class: com.android.support.TestNetworkRuleDialog$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() throws Throwable {
                        this.f$0.m1582lambda$show$4$comandroidsupportTestNetworkRuleDialog(testNetworkRule, file, textView, button, button2);
                    }
                }, "network-experiment").start();
            }
        } catch (Exception e) {
            textView.setText(e.getMessage());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0071  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00b6 A[PHI: r12
  0x00b6: PHI (r12v9 java.lang.String) = (r12v8 java.lang.String), (r12v23 java.lang.String) binds: [B:38:0x00b4, B:31:0x0099] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00d3  */
    /* renamed from: lambda$show$4$com-android-support-TestNetworkRuleDialog, reason: not valid java name */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    /* synthetic */ void m1582lambda$show$4$comandroidsupportTestNetworkRuleDialog(com.android.support.TestNetworkRule r12, java.io.File r13, final android.widget.TextView r14, final android.widget.Button r15, final android.widget.Button r16) throws java.lang.Throwable {
        /*
            r11 = this;
            java.lang.String r0 = "\n请点击恢复重试清理：\n"
            java.lang.String r1 = "实验未正常完成。\n"
            java.lang.String r2 = "实验结束，规则已清理。\n"
            java.lang.String r3 = "实验执行异常，请点击恢复重试："
            r4 = 0
            com.android.support.TestNetworkRuleDialog$RootRunner r5 = r11.runner     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            java.lang.String r6 = r13.getAbsolutePath()     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            java.lang.String r6 = r12.script(r6)     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            r7 = 90000(0x15f90, double:4.4466E-319)
            com.android.support.TestNetworkRuleDialog$Result r5 = r5.run(r6, r7)     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            boolean r6 = r5.success     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            java.lang.String r7 = "XF_NET_CLEARED"
            r8 = 1
            if (r6 == 0) goto L40
            java.lang.String r6 = r5.output     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            boolean r6 = r6.contains(r7)     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            if (r6 == 0) goto L40
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            r12.<init>(r2)     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            java.lang.String r0 = r5.output     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            java.lang.StringBuilder r12 = r12.append(r0)     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            java.lang.String r12 = r12.toString()     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            goto L99
        L3c:
            r0 = move-exception
            r12 = r0
            goto La3
        L40:
            boolean r2 = r13.exists()     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            if (r2 == 0) goto L58
            java.lang.String r2 = r5.output     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            java.lang.String r6 = "XF_NET_ACTIVE"
            boolean r2 = r2.contains(r6)     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            if (r2 != 0) goto L58
            boolean r2 = r5.success     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            if (r2 == 0) goto L58
            java.lang.String r12 = "实验已取消。"
            goto L99
        L58:
            com.android.support.TestNetworkRuleDialog$RootRunner r2 = r11.runner     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            java.lang.String r12 = r12.cleanupScript()     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            r9 = 20000(0x4e20, double:9.8813E-320)
            com.android.support.TestNetworkRuleDialog$Result r12 = r2.run(r12, r9)     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            boolean r2 = r12.success     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            if (r2 == 0) goto L71
            java.lang.String r2 = r12.output     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            boolean r2 = r2.contains(r7)     // Catch: java.lang.Throwable -> L9c java.lang.RuntimeException -> La0
            if (r2 == 0) goto L71
            goto L72
        L71:
            r8 = r4
        L72:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            r2.<init>(r1)     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            java.lang.String r1 = r5.output     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            java.lang.StringBuilder r1 = r2.append(r1)     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            if (r8 == 0) goto L82
            java.lang.String r12 = "\n本次规则已清理。"
            goto L91
        L82:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            r2.<init>(r0)     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            java.lang.String r12 = r12.output     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            java.lang.StringBuilder r12 = r2.append(r12)     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            java.lang.String r12 = r12.toString()     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
        L91:
            java.lang.StringBuilder r12 = r1.append(r12)     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
            java.lang.String r12 = r12.toString()     // Catch: java.lang.RuntimeException -> L3c java.lang.Throwable -> Lcf
        L99:
            if (r8 == 0) goto Lb9
            goto Lb6
        L9c:
            r0 = move-exception
            r12 = r0
            r8 = r4
            goto Ld1
        La0:
            r0 = move-exception
            r12 = r0
            r8 = r4
        La3:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lcf
            r0.<init>(r3)     // Catch: java.lang.Throwable -> Lcf
            java.lang.String r12 = r12.getMessage()     // Catch: java.lang.Throwable -> Lcf
            java.lang.StringBuilder r12 = r0.append(r12)     // Catch: java.lang.Throwable -> Lcf
            java.lang.String r12 = r12.toString()     // Catch: java.lang.Throwable -> Lcf
            if (r8 == 0) goto Lb9
        Lb6:
            r11.clearRecovery(r13)
        Lb9:
            java.util.concurrent.atomic.AtomicBoolean r13 = com.android.support.TestNetworkRuleDialog.RUNNING
            r13.set(r4)
            r8 = r12
            android.app.Activity r12 = r11.activity
            com.android.support.TestNetworkRuleDialog$$ExternalSyntheticLambda3 r5 = new com.android.support.TestNetworkRuleDialog$$ExternalSyntheticLambda3
            r6 = r11
            r7 = r14
            r9 = r15
            r10 = r16
            r5.<init>()
            r12.runOnUiThread(r5)
            return
        Lcf:
            r0 = move-exception
            r12 = r0
        Ld1:
            if (r8 == 0) goto Ld6
            r11.clearRecovery(r13)
        Ld6:
            java.util.concurrent.atomic.AtomicBoolean r11 = com.android.support.TestNetworkRuleDialog.RUNNING
            r11.set(r4)
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.TestNetworkRuleDialog.m1582lambda$show$4$comandroidsupportTestNetworkRuleDialog(com.android.support.TestNetworkRule, java.io.File, android.widget.TextView, android.widget.Button, android.widget.Button):void");
    }

    /* renamed from: lambda$show$3$com-android-support-TestNetworkRuleDialog, reason: not valid java name */
    /* synthetic */ void m1581lambda$show$3$comandroidsupportTestNetworkRuleDialog(TextView textView, String str, Button button, Button button2) {
        if (this.activity.isDestroyed() || this.activity.isFinishing()) {
            return;
        }
        textView.setText(str);
        button.setEnabled(this.pendingRule == null);
        button2.setEnabled(this.pendingRule != null);
    }

    boolean requestStop() throws IOException {
        File file = this.cancelFile;
        TestNetworkRule testNetworkRule = this.pendingRule;
        if (file == null && testNetworkRule != null) {
            file = new File(this.activity.getCacheDir(), "network-" + testNetworkRule.chain + ".cancel");
        }
        if (file == null) {
            return true;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            try {
                fileOutputStream.write(1);
                fileOutputStream.close();
                return true;
            } finally {
            }
        } catch (IOException unused) {
            return false;
        }
    }

    private void clearRecovery(File file) {
        if (this.recovery.edit().clear().commit()) {
            file.delete();
            this.cancelFile = null;
            this.pendingRule = null;
        }
    }

    private void retryCleanup(final TextView textView, final Button button, final Button button2) {
        final TestNetworkRule testNetworkRule = this.pendingRule;
        if (testNetworkRule == null || !RUNNING.compareAndSet(false, true)) {
            return;
        }
        button2.setEnabled(false);
        textView.setText("正在清理上次实验规则…");
        new Thread(new Runnable() { // from class: com.android.support.TestNetworkRuleDialog$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1580lambda$retryCleanup$7$comandroidsupportTestNetworkRuleDialog(testNetworkRule, textView, button, button2);
            }
        }, "network-recovery").start();
    }

    /* renamed from: lambda$retryCleanup$7$com-android-support-TestNetworkRuleDialog, reason: not valid java name */
    /* synthetic */ void m1580lambda$retryCleanup$7$comandroidsupportTestNetworkRuleDialog(TestNetworkRule testNetworkRule, final TextView textView, final Button button, final Button button2) {
        String str;
        try {
            try {
                Result resultRun = this.runner.run(testNetworkRule.cleanupScript(), 20000L);
                if (resultRun.success && resultRun.output.contains("XF_NET_CLEARED")) {
                    clearRecovery(new File(this.activity.getCacheDir(), "network-" + testNetworkRule.chain + ".cancel"));
                    str = "规则已清理。";
                } else {
                    str = "清理尚未完成，请再次恢复。\n" + resultRun.output;
                }
            } catch (RuntimeException e) {
                str = "清理执行异常：" + e.getMessage();
            }
            RUNNING.set(false);
            final String str2 = str;
            this.activity.runOnUiThread(new Runnable() { // from class: com.android.support.TestNetworkRuleDialog$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1579lambda$retryCleanup$6$comandroidsupportTestNetworkRuleDialog(textView, str2, button, button2);
                }
            });
        } catch (Throwable th) {
            RUNNING.set(false);
            throw th;
        }
    }

    /* renamed from: lambda$retryCleanup$6$com-android-support-TestNetworkRuleDialog, reason: not valid java name */
    /* synthetic */ void m1579lambda$retryCleanup$6$comandroidsupportTestNetworkRuleDialog(TextView textView, String str, Button button, Button button2) {
        if (this.activity.isDestroyed() || this.activity.isFinishing()) {
            return;
        }
        textView.setText(str);
        button.setEnabled(this.pendingRule == null);
        button2.setEnabled(this.pendingRule != null);
    }

    private void chooseService(AlertDialog alertDialog, TextView textView, EditText editText, EditText editText2, EditText editText3, Button button) {
        textView.setText("请先等待当前实验恢复完成。");
    }

    private /* synthetic */ void lambda$chooseService$11(TestNetworkService.Candidate[] candidateArr, final Button button, final TextView textView, final AlertDialog alertDialog, final EditText editText, final EditText editText2, final EditText editText3, DialogInterface dialogInterface, int i) {
        final TestNetworkService.Candidate candidate = candidateArr[i];
        button.setEnabled(false);
        textView.setText("正在通过本机网络解析 " + candidate.host + "…");
        new Thread(new Runnable() { // from class: com.android.support.TestNetworkRuleDialog$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() throws UnknownHostException {
                this.f$0.m1576xbc2c20f6(candidate, alertDialog, button, textView, editText, editText2, editText3);
            }
        }, "network-lookup").start();
    }

    /* renamed from: lambda$chooseService$10$com-android-support-TestNetworkRuleDialog, reason: not valid java name */
    /* synthetic */ void m1576xbc2c20f6(final TestNetworkService.Candidate candidate, final AlertDialog alertDialog, final Button button, final TextView textView, final EditText editText, final EditText editText2, final EditText editText3) throws UnknownHostException {
        final String message;
        final List<String> listResolve;
        try {
            listResolve = TestNetworkService.resolve(false, candidate, new TestNetworkService.Resolver() { // from class: com.android.support.TestNetworkRuleDialog$$ExternalSyntheticLambda6
                @Override // com.android.support.TestNetworkService.Resolver
                public final InetAddress[] resolve(String str) {
                    return InetAddress.getAllByName(str);
                }
            });
            message = null;
        } catch (Exception e) {
            message = e.getMessage();
            listResolve = null;
        }
        this.activity.runOnUiThread(new Runnable() { // from class: com.android.support.TestNetworkRuleDialog$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1578lambda$chooseService$9$comandroidsupportTestNetworkRuleDialog(alertDialog, button, listResolve, textView, message, candidate, editText, editText2, editText3);
            }
        });
    }

    /* renamed from: lambda$chooseService$9$com-android-support-TestNetworkRuleDialog, reason: not valid java name */
    /* synthetic */ void m1578lambda$chooseService$9$comandroidsupportTestNetworkRuleDialog(final AlertDialog alertDialog, Button button, final List list, final TextView textView, String str, final TestNetworkService.Candidate candidate, final EditText editText, final EditText editText2, final EditText editText3) {
        if (this.activity.isDestroyed() || this.activity.isFinishing() || !alertDialog.isShowing()) {
            return;
        }
        button.setEnabled(true);
        if (list == null) {
            textView.setText("服务解析失败：" + str);
        } else {
            new AlertDialog.Builder(this.activity).setTitle(candidate.host).setItems((CharSequence[]) list.toArray(new String[0]), new DialogInterface.OnClickListener() { // from class: com.android.support.TestNetworkRuleDialog$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.m1577lambda$chooseService$8$comandroidsupportTestNetworkRuleDialog(alertDialog, editText, list, editText2, candidate, editText3, textView, dialogInterface, i);
                }
            }).setNegativeButton("取消", (DialogInterface.OnClickListener) null).show();
        }
    }

    /* renamed from: lambda$chooseService$8$com-android-support-TestNetworkRuleDialog, reason: not valid java name */
    /* synthetic */ void m1577lambda$chooseService$8$comandroidsupportTestNetworkRuleDialog(AlertDialog alertDialog, EditText editText, List list, EditText editText2, TestNetworkService.Candidate candidate, EditText editText3, TextView textView, DialogInterface dialogInterface, int i) {
        if (alertDialog.isShowing() && !RUNNING.get() && this.pendingRule == null) {
            editText.setText((CharSequence) list.get(i));
            editText2.setText(Integer.toString(candidate.port));
            editText3.setText("TCP");
            textView.setText("已选择 " + candidate.host + "\n" + ((String) list.get(i)) + ":" + candidate.port + "\n尚未开始；此项只测试一个地址和端口。");
        }
    }

    private EditText input(LinearLayout linearLayout, String str, int i) {
        EditText editText = new EditText(this.activity);
        editText.setHint(str);
        editText.setInputType(i);
        editText.setSingleLine();
        linearLayout.addView(editText);
        return editText;
    }
}
