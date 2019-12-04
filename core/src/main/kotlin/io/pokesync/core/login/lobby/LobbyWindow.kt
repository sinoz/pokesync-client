package io.pokesync.core.login.lobby

import com.badlogic.gdx.scenes.scene2d.ui.*

/**
 * The lobby window.
 * @author Sino
 */
class LobbyWindow(skin: Skin) : Window("Lobby", skin, "lobby-window") {
    val queueState = QueueState(skin)

    val icon = PendingIcon(skin)

    val messageView = MessageView(skin)

    val scrollPane = ButtonedScrollPane(messageView, skin, "scroll-pane")

    val usernameLabel = UsernameLabel(skin)

    val inputField = MessageInputField(skin) {
        messageView.addMessageLabel("Sino", it)
    }

    val submitButton = SubmitButton(skin) {
        if (inputField.text.isNotEmpty()) {
            messageView.addMessageLabel("Sino", inputField.text)
            inputField.clearText()
        }
    }

    init {
        titleTable.getCell(titleLabel).padLeft(5F)
        titleTable.add(icon).right().padRight(5F)
        titleTable.add(queueState).padRight(5F)

        scrollPane.setForceScroll(false, true)
        scrollPane.setSmoothScrolling(false)
        scrollPane.setScrollBarPositions(false, false)

        isMovable = false
        isResizable = true

        add(scrollPane)
            .expand()
            .fill()
            .left()
            .bottom()
            .colspan(3)
            .padLeft(5F)
            .width(600F)
            .height(300F)
            .minWidth(600F)
            .minHeight(300F)
            .maxWidth(600F)
            .maxHeight(300F)
            .row()

        add(Image(skin, "separator"))
            .expandX()
            .fillX()
            .colspan(3)
            .row()

        add(usernameLabel)
            .pad(5F)

        add(inputField)
            .expandX()
            .fillX()
            .padTop(5F)
            .padBottom(5F)

        add(submitButton)
            .pad(5F)
            .right()
    }
}