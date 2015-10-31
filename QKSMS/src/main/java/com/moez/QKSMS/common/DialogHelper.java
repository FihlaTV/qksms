package com.moez.QKSMS.common;

import android.util.Log;
import com.moez.QKSMS.R;
import com.moez.QKSMS.data.Conversation;
import com.moez.QKSMS.transaction.SmsHelper;
import com.moez.QKSMS.ui.MainActivity;
import com.moez.QKSMS.ui.dialog.DefaultSmsHelper;
import com.moez.QKSMS.ui.dialog.QKDialog;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DialogHelper {
    private static final String TAG = "DialogHelper";

    public static void showDeleteConversationDialog(MainActivity context, long threadId) {
        Set<Long> threadIds = new HashSet<>();
        threadIds.add(threadId);
        showDeleteConversationsDialog(context, threadIds);
    }

    public static void showDeleteConversationsDialog(final MainActivity context, final Set<Long> threadIds) {

        new DefaultSmsHelper(context, R.string.not_default_delete).showIfNotDefault(null);

        new QKDialog()
                .setContext(context)
                .setTitle(R.string.delete_conversation)
                .setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.yes, v -> {
                    Log.d(TAG, "Deleting threads: " + Arrays.toString(threadIds.toArray()));
                    Conversation.ConversationQueryHandler handler = new Conversation.ConversationQueryHandler(context.getContentResolver());

                    Conversation.startDelete(handler, 0, false, threadIds);
                    Conversation.asyncDeleteObsoleteThreads(handler, 0);
                    context.showMenu();
                }).setNegativeButton(R.string.cancel, null)
                .show(context.getFragmentManager(), QKDialog.CONFIRMATION_TAG);

    }

    public static void showDeleteFailedMessagesDialog(final MainActivity context, final Set<Long> threadIds) {
        new DefaultSmsHelper(context, R.string.not_default_delete).showIfNotDefault(null);

        new QKDialog()
                .setContext(context)
                .setTitle(R.string.delete_all_failed)
                .setMessage(R.string.delete_all_failed_confirmation)
                .setPositiveButton(R.string.yes, v -> {
                    new Thread(() -> {
                        for (long threadId : threadIds) {
                            SmsHelper.deleteFailedMessages(context, threadId);
                        }
                    }).start();
                }).setNegativeButton(R.string.cancel, null)
                .show(context.getFragmentManager(), QKDialog.CONFIRMATION_TAG);
    }

}
