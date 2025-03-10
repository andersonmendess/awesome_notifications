//
//  NotificationBuilder.swift
//  awesome_notifications
//
//  Created by Rafael Setragni on 11/09/20.
//

import Foundation

@available(iOS 10.0, *)
public class NotificationBuilder {
    
    private static let TAG = "NotificationBuilder"
    
    // ************** FACTORY PATTERN ***********************

    public static func newInstance() -> NotificationBuilder {
        return NotificationBuilder()
    }
    private init(){}
    
    // ********************************************************
    
    public func jsonDataToNotificationModel(jsonData:[String : Any?]?) -> NotificationModel? {
        if jsonData?.isEmpty ?? true { return nil }

        let notificationModel:NotificationModel? = NotificationModel().fromMap(arguments: jsonData!) as? NotificationModel
        return notificationModel
    }
    
    public func jsonToNotificationModel(jsonData:String?) -> NotificationModel? {
        if StringUtils.isNullOrEmpty(jsonData) { return nil }
        
        let data:[String:Any?]? = JsonUtils.fromJson(jsonData)
        if data == nil { return nil }
        
        let notificationModel:NotificationModel? = NotificationModel().fromMap(arguments: data!) as? NotificationModel
        return notificationModel
    }
    
    public func buildNotificationFromJson(jsonData:String?) -> NotificationModel? {
        return  jsonToNotificationModel(jsonData: jsonData)
    }
    
    public func buildNotificationActionFromModel(
        notificationModel:NotificationModel?,
        buttonKeyPressed:String?,
        userText:String?
    ) -> ActionReceived? {
        if notificationModel == nil { return nil }
        let actionReceived:ActionReceived = ActionReceived(
            notificationModel!.content,
            buttonKeyPressed: buttonKeyPressed,
            buttonKeyInput: userText)
        
        if notificationModel!.actionButtons != nil && !StringUtils.isNullOrEmpty(buttonKeyPressed) {
            for button:NotificationButtonModel in notificationModel!.actionButtons! {
                if button.key == buttonKeyPressed {
                    actionReceived.autoDismissible = button.autoDismissible
                    actionReceived.actionType = button.actionType
                    break
                }
            }
        }
        
        if notificationModel!.schedule == nil {
            actionReceived.displayedDate = actionReceived.createdDate
            actionReceived.displayedLifeCycle = actionReceived.createdLifeCycle
        }
        
        return actionReceived
    }
    
    public func createNotification(_ notificationModel:NotificationModel, content:UNMutableNotificationContent?) throws -> NotificationModel? {
        
        guard let channel = ChannelManager.shared.getChannelByKey(channelKey: notificationModel.content!.channelKey!) else {
            throw AwesomeNotificationsException.invalidRequiredFields(msg: "Channel '\(notificationModel.content!.channelKey!)' does not exist or is disabled")
        }

        notificationModel.content!.groupKey = getGroupKey(notificationModel: notificationModel, channel: channel)

        let nextDate:RealDateTime? = getNextScheduleDate(notificationModel: notificationModel)
        
        if(notificationModel.schedule == nil || nextDate != nil){
            
            let content = content ?? buildNotificationContentFromModel(notificationModel: notificationModel)
            
            setTitle(notificationModel: notificationModel, channel: channel, content: content)
            setBody(notificationModel: notificationModel, content: content)
            setSummary(notificationModel: notificationModel, content: content)

            setGrouping(notificationModel: notificationModel, channel: channel, content: content)
            
            setVisibility(notificationModel: notificationModel, channel: channel, content: content)
            setShowWhen(notificationModel: notificationModel, content: content)
            setBadgeIndicator(notificationModel: notificationModel, channel: channel, content: content)
            
            setAutoCancel(notificationModel: notificationModel, content: content)
            setTicker(notificationModel: notificationModel, content: content)
            
            setOnlyAlertOnce(notificationModel: notificationModel, channel: channel, content: content)
            
            setLockedNotification(notificationModel: notificationModel, channel: channel, content: content)
            setImportance(channel: channel, notificationModel: notificationModel,content: content)
            
            setSound(notificationModel: notificationModel, channel: channel, content: content)
            setVibrationPattern(channel: channel, content: content)
            
            setLights(channel: channel, content: content)
            
            setSmallIcon(channel: channel, content: content)
            setLargeIcon(notificationModel: notificationModel, content: content)
            
            setLayoutColor(notificationModel: notificationModel, channel: channel, content: content)
            
            setLayout(notificationModel: notificationModel, content: content)
            
            createActionButtonsAndCategory(notificationModel: notificationModel, content: content)
            
            setWakeUpScreen(notificationModel: notificationModel, content: content)
            setCriticalAlert(channel: channel, content: content)
            
            setUserInfoContent(notificationModel: notificationModel, content: content)
            
            if SwiftUtils.isRunningOnExtension() {
                return notificationModel
            }
            
            notificationModel.nextValidDate = nextDate
            
            let trigger:UNNotificationTrigger? = nextDate == nil ? nil : notificationModel.schedule?.getUNNotificationTrigger()
            let request = UNNotificationRequest(identifier: notificationModel.content!.id!.description, content: content, trigger: trigger)
            
            UNUserNotificationCenter.current().add(request)
            {
                error in // called when message has been sent

                if error != nil {
                    debugPrint("Error: \(error.debugDescription)")
                }
                else {
                    if(notificationModel.schedule != nil){
                        
                        notificationModel.schedule!.timeZone =
                            notificationModel.schedule!.timeZone ?? TimeZone.current
                        notificationModel.schedule!.createdDate = RealDateTime(
                            fromTimeZone: notificationModel.schedule!.timeZone!)
                        
                        if (nextDate != nil){
                            ScheduleManager.saveSchedule(notification: notificationModel, nextDate: nextDate!.date)
                        } else {
                            _ = ScheduleManager.removeSchedule(id: notificationModel.content!.id!)
                        }
                    }
                }
            }
            return notificationModel
        }
        else {
            if(notificationModel.schedule != nil){
                _ = ScheduleManager.removeSchedule(id: notificationModel.content!.id!)
            }
        }
        
        return nil
    }
    
    private func dateToCalendarTrigger(targetDate:Date?) -> UNCalendarNotificationTrigger? {
        if(targetDate == nil){ return nil}
        
        let dateComponents = Calendar.current.dateComponents([.day, .month, .year, .hour, .minute, .second], from: targetDate!)
        let trigger = UNCalendarNotificationTrigger( dateMatching: dateComponents, repeats: false )
        return trigger
    }
    
    private func buildNotificationContentFromModel(notificationModel:NotificationModel) -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        return content
    }
    
    public func setUserInfoContent(notificationModel:NotificationModel, content:UNMutableNotificationContent) {
        
        let pushData = notificationModel.toMap()
        let jsonData = JsonUtils.toJson(pushData)

        content.userInfo[Definitions.NOTIFICATION_JSON] = jsonData
        content.userInfo[Definitions.NOTIFICATION_ID] = notificationModel.content!.id!
        content.userInfo[Definitions.NOTIFICATION_CHANNEL_KEY] = notificationModel.content!.channelKey!
        content.userInfo[Definitions.NOTIFICATION_GROUP_KEY] = notificationModel.content!.groupKey
    }

    private func setTitle(notificationModel:NotificationModel, channel:NotificationChannelModel, content:UNMutableNotificationContent){
        content.title = notificationModel.content!.title?.withoutHtmlTags() ?? ""
    }
    
    private func setBody(notificationModel:NotificationModel, content:UNMutableNotificationContent){
        content.body = notificationModel.content!.body?.withoutHtmlTags() ?? ""
    }
    
    private func setSummary(notificationModel:NotificationModel, content:UNMutableNotificationContent){
        if #available(iOS 12.0, *) {
            content.summaryArgument = notificationModel.content!.summary?.withoutHtmlTags() ?? ""
        }
    }
    
    private func setBadgeIndicator(notificationModel:NotificationModel, channel:NotificationChannelModel, content:UNMutableNotificationContent){
        if(channel.channelShowBadge!){
            content.badge = NSNumber(value: BadgeManager.shared.incrementGlobalBadgeCounter())
        }
    }
    
    private func createActionButtonsAndCategory(notificationModel:NotificationModel, content:UNMutableNotificationContent){
        
        var categoryIdentifier:String = StringUtils.isNullOrEmpty(content.categoryIdentifier) ?
            Definitions.DEFAULT_CATEGORY_IDENTIFIER : content.categoryIdentifier
        
        var actions:[UNNotificationAction] = []
        var dynamicCategory:[String] = []
        var dynamicLabels:[String] = []
        
        if(notificationModel.actionButtons != nil){
            
            var temporaryCategory:[String] = []
            
            dynamicCategory.append(content.categoryIdentifier)
            
            for button in notificationModel.actionButtons! {
                
                let action:UNNotificationAction?
                var options:UNNotificationActionOptions = []
                
                if button.actionType == .Default {
                    options.update(with: .foreground)
                }
                
                if button.isDangerousOption ?? false {
                    options.update(with: .destructive)
                }
                
                if button.requireInputText ?? false {
                    action = UNTextInputNotificationAction(
                        identifier: button.key!,
                        title: button.label!,
                        options: options
                    )
                }
                else {
                    action = UNNotificationAction(
                        identifier: button.key!,
                        title: button.label!,
                        options: options
                    )
                }
                
                temporaryCategory.append(button.key!)
                dynamicLabels.append(
                    button.label! +
                    (button.actionType?.rawValue ?? "default"))
                actions.append(action!)
            }
            
            dynamicCategory.append(contentsOf: temporaryCategory)
            
            categoryIdentifier = dynamicCategory.joined(separator: ",")
        }
        
        categoryIdentifier = categoryIdentifier.uppercased()

        if(AwesomeNotifications.debug){
            print("Notification category identifier: " + categoryIdentifier)
        }
        content.categoryIdentifier = categoryIdentifier
        
        dynamicLabels.append(contentsOf: dynamicCategory)
        let categoryHashIdentifier = dynamicLabels.joined(separator: ",").md5
                
        let userDefaults = UserDefaults(suiteName: Definitions.USER_DEFAULT_TAG)
        let lastHash = userDefaults!.string(forKey: categoryHashIdentifier)
        
        
        // Only calls setNotificationCategories when its necessary to update or create it
        if(StringUtils.isNullOrEmpty(lastHash)){
            
            userDefaults!.set(categoryHashIdentifier, forKey: "registered")
            
            let categoryObject = UNNotificationCategory(
                identifier: categoryIdentifier,
                actions: actions,
                intentIdentifiers: [],
                options: .customDismissAction
            )
            
            UNUserNotificationCenter.current().getNotificationCategories(completionHandler: { results in
                UNUserNotificationCenter.current().setNotificationCategories(results.union([categoryObject]))
            })
        }
    }
    
    private func getNextScheduleDate(notificationModel:NotificationModel?) -> RealDateTime? {
        
        if notificationModel?.schedule == nil { return nil }
        var nextDate:Date?
        switch true {
            
            case notificationModel!.schedule! is NotificationCalendarModel:
                
                let calendarModel:NotificationCalendarModel = notificationModel!.schedule! as! NotificationCalendarModel
                guard let trigger:UNCalendarNotificationTrigger = calendarModel.getUNNotificationTrigger() as? UNCalendarNotificationTrigger else { return nil }
                
                nextDate = trigger.nextTriggerDate()
                
            case notificationModel!.schedule! is NotificationIntervalModel:
                
                let intervalModel:NotificationIntervalModel = notificationModel!.schedule! as! NotificationIntervalModel
                guard let trigger:UNTimeIntervalNotificationTrigger = intervalModel.getUNNotificationTrigger() as? UNTimeIntervalNotificationTrigger else { return nil }
                
                nextDate = trigger.nextTriggerDate()
                
            default:
                break
        }
        
        if nextDate == nil {
            return nil
        }
        
        return RealDateTime.init(fromDate: nextDate!, inTimeZone: notificationModel!.schedule!.timeZone!)
    }
    
    private func setVisibility(notificationModel:NotificationModel, channel:NotificationChannelModel, content:UNMutableNotificationContent){
        // TODO
    }
    
    private func setShowWhen(notificationModel:NotificationModel, content:UNMutableNotificationContent){
        // TODO
    }

    private func setAutoCancel(notificationModel:NotificationModel, content:UNMutableNotificationContent){
        // TODO
    }
    
    private func setTicker(notificationModel:NotificationModel, content:UNMutableNotificationContent){
        // TODO
    }
    
    private func setOnlyAlertOnce(notificationModel:NotificationModel, channel:NotificationChannelModel, content:UNMutableNotificationContent){
        // TODO
    }

    private func setLockedNotification(notificationModel:NotificationModel, channel:NotificationChannelModel, content:UNMutableNotificationContent){
        // TODO
    }
    
    private func setImportance(channel:NotificationChannelModel, notificationModel:NotificationModel, content:UNMutableNotificationContent){
        notificationModel.importance = notificationModel.importance ?? channel.importance ?? .Default
        if #available(iOS 15.0, *) {
            switch notificationModel.importance! {
                
            case .None, .Min, .Low:
                content.interruptionLevel = .passive
                break
                
            case .Default:
                content.interruptionLevel = .active
                break
                
            case .High, .Max:
                content.interruptionLevel = .timeSensitive
                break
            }
        }
    }

    private func setSound(notificationModel:NotificationModel, channel:NotificationChannelModel, content:UNMutableNotificationContent){
        
        switch notificationModel.importance ?? .Default {
            
            case .Default, .High, .Max:
            if (notificationModel.content!.playSound ?? false) && (channel.playSound ?? false) {
                
                if(!StringUtils.isNullOrEmpty(notificationModel.content!.customSound)){
                    content.sound = AudioUtils.shared.getSoundFromSource(SoundPath: notificationModel.content!.customSound!)
                    return
                }
                
                if(!StringUtils.isNullOrEmpty(channel.soundSource)){
                    content.sound = AudioUtils.shared.getSoundFromSource(SoundPath: channel.soundSource!)
                    return
                }
                
                // TODO Get default iOS path sounds
                switch channel.defaultRingtoneType {
                    
                    case .Ringtone:
                        content.sound = UNNotificationSound.default
                        return
                        
                    case .Alarm:
                        content.sound = UNNotificationSound.default
                        return
                    
                    case .Notification:
                        content.sound = UNNotificationSound.default
                        return
                        
                    case .none:
                        content.sound = UNNotificationSound.default
                        return
                }
            }
            else {
                content.sound = nil
            }
            break
            
            default:
            break
        }
    }
    
    private func setVibrationPattern(channel:NotificationChannelModel, content:UNMutableNotificationContent){
        // TODO
    }
    
    private func setLights(channel:NotificationChannelModel, content:UNMutableNotificationContent){
        // iOS does not have any lights
    }
    
    private func setWakeUpScreen(notificationModel:NotificationModel, content:UNMutableNotificationContent){
        if notificationModel.content?.wakeUpScreen ?? false {
            if #available(iOS 15.0, *) {
                content.interruptionLevel = .timeSensitive
            }
        }
    }
    
    private func setCriticalAlert(channel:NotificationChannelModel, content:UNMutableNotificationContent){
        if channel.criticalAlerts ?? false {
            if #available(iOS 15.0, *) {
                content.interruptionLevel = .critical
            }
        }
    }

    private func setSmallIcon(channel:NotificationChannelModel, content:UNMutableNotificationContent){
        // TODO
    }
    
    private func setLargeIcon(notificationModel:NotificationModel, content:UNMutableNotificationContent){
        // TODO
    }
    
    private func setLayoutColor(notificationModel:NotificationModel, channel:NotificationChannelModel, content:UNMutableNotificationContent){
        // TODO
    }

    private func setGrouping(notificationModel:NotificationModel, channel:NotificationChannelModel, content:UNMutableNotificationContent){

        let groupKey:String? = getGroupKey(notificationModel: notificationModel, channel: channel)
        if(!StringUtils.isNullOrEmpty(groupKey)){
            content.threadIdentifier = groupKey!
        }
    }

    private func getGroupKey(notificationModel:NotificationModel, channel:NotificationChannelModel) -> String? {
        return notificationModel.content!.groupKey ?? channel.groupKey
    }

    private func setLayout(notificationModel:NotificationModel, content:UNMutableNotificationContent){
        
        switch notificationModel.content!.notificationLayout {
            
            case .BigPicture:
                setBigPictureLayout(notificationModel: notificationModel, content: content)
                return
                
            case .BigText:
                setBigTextLayout(notificationModel: notificationModel, content: content)
                return
                
            case .Inbox:
                setInboxLayout(notificationModel: notificationModel, content: content)
                return
                
            case .MediaPlayer:
                setMediaPlayerLayout(notificationModel: notificationModel, content: content)
                return
                
            case .Messaging:
                setMessagingLayout(notificationModel: notificationModel, content: content, isGrouping: false)
                return
                
            case .MessagingGroup:
                setMessagingLayout(notificationModel: notificationModel, content: content, isGrouping: true)
                return
                        
            case .ProgressBar:
                setProgressBarLayout(notificationModel: notificationModel, content: content)
                return
                            
            case .Default:
                setDefaultLayout(notificationModel: notificationModel, content: content)
                return
            
            default:
                setDefaultLayout(notificationModel: notificationModel, content: content)
                return
        }
    }
    
    private func getBitmapAttatchment(from bitmapSource:String?, rounding roundedBitmap: Bool) -> UNNotificationAttachment? {
        
        //let dimensionLimit:CGFloat = 1038.0
        		
        if !StringUtils.isNullOrEmpty(bitmapSource) {
            
            if let image:UIImage =
                BitmapUtils
                    .shared
                    .getBitmapFromSource(
                        bitmapPath: bitmapSource!,
                        roundedBitpmap: roundedBitmap
            ){
                
                let fileManager = FileManager.default
                let tmpSubFolderName = ProcessInfo.processInfo.globallyUniqueString
                let tmpSubFolderURL = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent(tmpSubFolderName, isDirectory: true)
                
                do {
                    try fileManager.createDirectory(at: tmpSubFolderURL, withIntermediateDirectories: true, attributes: nil)
                    let imageFileIdentifier = bitmapSource!.md5 + ".png"
                    let fileURL = tmpSubFolderURL.appendingPathComponent(imageFileIdentifier)
                    
                    // JPEG is more memory efficient, but switches trasparency by white color
                    let imageData = image.pngData()//.jpegData(compressionQuality: 0.9)//
                    try imageData?.write(to: fileURL)
                                        
                    let imageAttachment = try UNNotificationAttachment.init(identifier: imageFileIdentifier, url: fileURL, options: nil)
                    return imageAttachment
                    
                } catch {
                    print("error " + error.localizedDescription)
                }
            }
        }
        return nil
    }
    
    private func setBigPictureLayout(notificationModel:NotificationModel, content:UNMutableNotificationContent) {
        content.categoryIdentifier = "BigPicture"
        
        if(!StringUtils.isNullOrEmpty(notificationModel.content?.bigPicture)){
            
            if let attachment:UNNotificationAttachment =
                getBitmapAttatchment(
                    from: notificationModel.content?.bigPicture,
                    rounding: notificationModel.content?.roundedBigPicture ?? false
            ){
                content.attachments.append(attachment)
                return
            }
        }
        
        if(!StringUtils.isNullOrEmpty(notificationModel.content?.largeIcon)){
            
            if let attachment:UNNotificationAttachment =
                    getBitmapAttatchment(
                        from: notificationModel.content?.largeIcon,
                        rounding: notificationModel.content?.roundedLargeIcon ?? false
            ){
                content.attachments.append(attachment)
            }
        }
    }
    
    private func setBigTextLayout(notificationModel:NotificationModel, content:UNMutableNotificationContent) {
        content.categoryIdentifier = "BigText"
    }
    
    private func setProgressBarLayout(notificationModel:NotificationModel, content:UNMutableNotificationContent) {
        content.categoryIdentifier = "ProgressBar"
    }
    
    private func setIndeterminateBarLayout(notificationModel:NotificationModel, content:UNMutableNotificationContent) {
        content.categoryIdentifier = "IndeterminateBar"
    }
    
    private func setMediaPlayerLayout(notificationModel:NotificationModel, content:UNMutableNotificationContent) {
        content.categoryIdentifier = "MediaPlayer"
    }
    
    private func setInboxLayout(notificationModel:NotificationModel, content:UNMutableNotificationContent) {
        content.categoryIdentifier = "Inbox"
    }
    
    private func setMessagingLayout(notificationModel:NotificationModel, content:UNMutableNotificationContent, isGrouping:Bool) {
        content.categoryIdentifier = "Messaging"
        
        content.threadIdentifier = (isGrouping ? "MessagingGR." : "Messaging.")+notificationModel.content!.channelKey!
    }
    
    private func setDefaultLayout(notificationModel:NotificationModel, content:UNMutableNotificationContent) {
        content.categoryIdentifier = "Default"
    }
    
}
