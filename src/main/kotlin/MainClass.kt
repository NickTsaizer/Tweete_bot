import com.xenomachina.argparser.ArgParser
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.MessageUpdateEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.core.requests.restaction.pagination.ReactionPaginationAction
import javax.security.auth.login.LoginException

val emote = "E:532807314168610866"
var movielist = mutableListOf<String>()

fun addToList(movie:String) {
    if (!movielist.contains(movie)) movielist.add(movie)
}

class MainListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent?) {
        val author = event!!.author
        val message = event.message
        val msg = message.contentDisplay
        val bot = author.isBot
        if (event.isFromType(ChannelType.TEXT) && !bot)
        when {
            msg.endsWith(":film_frames1:") -> {
                addToList(msg.dropWhile { it != ' ' })
                println("${author.name}, фильм добавлен:${msg.dropWhile { it != ' ' }}")
                message.addReaction(emote).queue()
            }
            msg.startsWith("@КИНМАН") -> {
                addToList(msg.dropWhile { it != ' ' })
                println("${author.name}, фильм добавлен:${msg.dropWhile { it != ' ' }}")
                message.addReaction(emote).queue()
            }
        }
    }
    override fun onMessageUpdate(event: MessageUpdateEvent?) {
        val author = event!!.author
        val message = event.message
        val msg = message.contentDisplay
        val bot = author.isBot
        if (event.isFromType(ChannelType.TEXT) && !bot)
            when {
                msg.endsWith(":film_frames1:") -> {
                    if (!msg.startsWith("@КИНМАН")) {
                        addToList(msg.dropLast(14))
                        println("${author.name}, фильм добавлен: ${msg.dropLast(14)}")
                        message.addReaction(emote).queue()
                    }
                }
            }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent?) {
        val ID = event!!.reaction.messageId
        val channel = event.channel
        val message = channel.getMessageById(ID).complete()
        val author = message.author
        if (event.isFromType(ChannelType.TEXT))
            when {
                event.reactionEmote.name == "film_frames1" && checkUsers(event.reaction.users)-> {
                    addToList()
                    println("${author.name}, фильм добавлен: ${message.contentDisplay}")
                    message.addReaction(event.reactionEmote.emote).queue()
                    println(event.reactionEmote.emote)
                }
            }
    }
//  https://discordapp.com/api/oauth2/authorize?client_id=538944501134131208&scope=bot&permissions=268643392
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val parser = ArgParser(args).parseInto(::Args)
            try {
                val jda =
                        JDABuilder(parser.token)
                                .addEventListener(MainListener())
                                .build()
                println("Token "+parser.token+" accepted.")
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

class Args(parser: ArgParser) {
    val token by parser.storing("-t", "--token",
            help = "server token")
}

fun checkUsers(users:ReactionPaginationAction) : Boolean {
    users.forEach {
        if (it.isBot) return false
    }
    return true
}