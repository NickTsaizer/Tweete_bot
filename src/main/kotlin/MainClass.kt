import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.MessageUpdateEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import javax.security.auth.login.LoginException

val chanel = "тестирование"
val emoji = "twitter"
val emote = "E:504627592213561354"


val messageList = listOf<message>()
data class message(
        val discordMessageID:String,
        val tweetID: Long? = null)
fun addToList(discordMessageID: String) {

}

class MainListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent?) {
        val author = event!!.author
        val ID = event.messageId
        val message = event.message
        val channel = event.channel
        val msg = message.contentDisplay
        val bot = author.isBot
        if (event.isFromType(ChannelType.TEXT) && !bot && channel.name==chanel)
        when {
            msg == "!ping" -> {
                channel.sendMessage("pong!").queue()
            }
            msg.endsWith(":twitter:") -> {
                addToList(ID)
                println("${author.name} tweeted: $msg")
                message.addReaction(emote).queue()
            }
            msg.startsWith("@Twitte") -> {
                addToList(ID)
                println("${author.name} tweeted: $msg")
                message.addReaction(emote).queue()
            }
        }
    }
    override fun onMessageUpdate(event: MessageUpdateEvent?) {
        val author = event!!.author
        val message = event.message
        val channel = event.channel
        val msg = message.contentDisplay
        val bot = author.isBot
        if (event.isFromType(ChannelType.TEXT) && !bot && channel.name == chanel)
            when {
                msg.endsWith(":twitter:") -> {
                    println("${author.name} tweeted: $msg")
                    message.addReaction(emote).queue()
                }
            }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent?) {
        val ID = event!!.reaction.messageId
        val channel = event.channel
        val message = channel.getMessageById(ID).complete()
        val author = message.author
        if (event.isFromType(ChannelType.TEXT) && channel.name == chanel)
            when {
                event.reactionEmote.name == "twitter" -> {
                    event.reaction.users.forEach {
                        if (!it.isBot) {
                            println("${author.name} tweeted: $message")
                            message.addReaction(emote).queue()
                        }
                    }
                }
            }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val jda =
                        JDABuilder("NTA0NjQ4Mjk5ODIzODkwNDM1.DrLycg.AvmJ0gOZDQGKeQ_QHUJKs99x5zM")
                                .addEventListener(MainListener())
                                .build()
                jda.awaitReady()

                println("Finished Building JDA!")
            } catch (e: LoginException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}