package com.maximilianwiegmann.backend.statistics;

import java.lang.reflect.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import com.maximilianwiegmann.backend.account.AccountService;
import com.maximilianwiegmann.backend.chat.ChatMessage;
import com.maximilianwiegmann.backend.group.GroupService;
import com.maximilianwiegmann.backend.group.data.GroupData;
import com.maximilianwiegmann.backend.group.data.GroupMember;
import com.maximilianwiegmann.backend.notifications.Notification;
import com.maximilianwiegmann.backend.notifications.NotificationHandler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StatisticHandler {

    private final GroupService groupService;
    private final AccountService accountService;
    private final NotificationHandler notificationHandler;

    public JSONObject get(long from, long to) {
        JSONObject json = new JSONObject();

        for (StatsType value : StatsType.values()) {
            json.put(value.name(), new JSONArray(getStatsObject(value, from, to)));
        }

        return json;
    }

    private List<StatsObject> getStatsObject(StatsType type, long from, long to) {
        List<TrendItem> trends = new ArrayList<>();
        switch (type) {
            case GROUPS -> {
                List<GroupData> groups = groupService.getGroups(from, to);
                List<StatsItem> groupStats = groups.stream()
                        .map(group -> new StatsItem(group.getCreated())).toList();

                List<StatsItem> groupMemberStats = new ArrayList<>();

                int groupAmount = groups.size();
                int groupMember = 0;
                for (GroupData group : groups) {
                    groupMember += group.getMember().size();

                    for (GroupMember member : group.getMember()) {
                        groupMemberStats.add(new StatsItem(member.getTimestamp()));
                    }
                }
                double average = groupAmount == 0 ? 0 : groupMember / groupAmount;

                List<GroupData> previousGroups = groupService.getGroups(from - (to - from), from);

                int previousGroupAmount = previousGroups.size();
                int previousGroupMember = 0;
                for (GroupData group : previousGroups) {
                    previousGroupMember += group.getMember().size();
                }
                double previousAverage = previousGroupAmount == 0 ? previousGroupAmount
                        : previousGroupMember / previousGroupAmount;

                TrendItem averageMember = new TrendItem(calculatePercentageChange(previousAverage, average), average,
                        previousAverage, TrendType.AVERAGE_GROUP_MEMBER);
                trends.add(averageMember);

                return Arrays.asList(new StatsObject(groupAmount, groupStats, new ArrayList<>()),
                        new StatsObject(groupMember, groupMemberStats, trends));
            }
            case ACCOUNTS -> {
                List<StatsItem> accounts = accountService.getAccounts(from, to).stream()
                        .map(account -> new StatsItem(account.getTimestamp())).toList();

                return Arrays.asList(new StatsObject(accounts.size(), accounts, trends));
            }
            case MESSAGES -> {
                List<ChatMessage> messages = groupService.getMessages(from, to);
                List<ChatMessage> previousMessages = groupService.getMessages(from - (to - from), from);

                List<StatsItem> messageStats = messages.stream()
                        .map(message -> new StatsItem(message.getTimestamp())).toList();

                List<StatsItem> previousMessageStats = previousMessages.stream()
                        .map(message -> new StatsItem(message.getTimestamp())).toList();

                TrendItem totalMessages = new TrendItem(
                        calculatePercentageChange(previousMessageStats.size(), messageStats.size()),
                        messageStats.size(), previousMessageStats.size(), TrendType.TOTAL_MESSAGES);
                trends.add(totalMessages);

                double averageMessageCount = this.getMessageProUserAverage(messages);
                double previousAMC = this.getMessageProUserAverage(previousMessages);

                TrendItem messageProUser = new TrendItem(calculatePercentageChange(previousAMC, averageMessageCount),
                        averageMessageCount, previousAMC, TrendType.MESSAGE_PRO_USER);
                trends.add(messageProUser);

                return Arrays.asList(new StatsObject(messageStats.size(), messageStats, trends));
            }
            case NOTIFICATIONS -> {
                List<Notification> notifications = notificationHandler.getNotifications(from, to);
                List<StatsItem> notificationStats = notifications.stream()
                        .map(notification -> new StatsItem(notification.getTimestamp())).toList();

                int notificationThisPeriod = notifications.size();

                List<Notification> previousNotifications = notificationHandler.getNotifications(from - (to - from), from);
                int previousNotification = previousNotifications.size();

                double percentage = calculatePercentageChange(previousNotification, notificationThisPeriod);

                TrendItem notificationTrend = new TrendItem(percentage, notificationThisPeriod, previousNotification, TrendType.TOTAL_NOTIFICATIONS);
                trends.add(notificationTrend);

                return Arrays.asList(new StatsObject(notificationStats.size(), notificationStats, trends));
            }
            case ACTIVITY -> {

                return new ArrayList<>();
            }
            default -> {
                return null;
            }
        }
    }

    private double getMessageProUserAverage(List<ChatMessage> messages) {
        if (messages.size() == 0)
            return 0;
        Map<String, Integer> userMessages = new HashMap<>();
        for (ChatMessage message : messages) {
            userMessages.putIfAbsent(message.getUserId(), 0);
            int currentCount = userMessages.get(message.getUserId());
            userMessages.put(message.getUserId(), currentCount + 1);
        }
        int totalUsers = userMessages.size();
        int totalMessages = 0;

        for (int count : userMessages.values()) {
            totalMessages += count;
        }

        return (double) totalMessages / totalUsers;
    }

    private double calculatePercentageChange(double initialValue, double finalValue) {
        if (initialValue == 0)
            return 0;
        return ((finalValue - initialValue) / initialValue) * 100;
    }

    @RequiredArgsConstructor
    @Getter
    public class StatsObject {
        private final int count;
        private final List<StatsItem> data;
        private final List<TrendItem> trends;
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    @Getter
    public class StatsItem {
        private final long timestamp;
        private int amount;
    }

    @RequiredArgsConstructor
    @Getter
    public class TrendItem {
        private final double percentagePeriod;
        private final double amountPeriod;
        private final double amountLastPeriod;
        private final TrendType trendType;
    }

    @RequiredArgsConstructor
    @Getter
    public enum TrendType {
        NEW_USERS(StatsType.ACCOUNTS),
        AVERAGE_GROUP_MEMBER(StatsType.GROUPS),
        MESSAGE_PRO_USER(StatsType.MESSAGES),
        TOTAL_MESSAGES(StatsType.MESSAGES),
        TOTAL_NOTIFICATIONS(StatsType.NOTIFICATIONS);

        private final StatsType statsType;
    }

    public enum StatsType {
        GROUPS, ACCOUNTS, MESSAGES, FILES, EVENTS, NOTIFICATIONS, ACTIVITY
    }

}
