package com.example.nertzlayout2

import android.graphics.Point


class GameBoardLayoutParams(
    val minSideMargin: Float,
    val betweenCardPadding: Float,
    val playerMaxWidth: Float,
    val playerAspectRatio: Float,
    val numPlayerPiles: Int,
    val cascadeOverlapFraction: Float,
    val aceWidthFraction: Float,
    val aceAspectRatio: Float,
    val betweenAcesAndPiles: Float
)

class GameBoardLayout(
    val aceLocations: Array<Point>,
    val aceWidth: Int,
    val aceHeight: Int,

    val playerTop: Int,
    val nertzLocation: Point,
    val cascadeLocations: Array<Point>,
    val cascadeOverlapSize: Int,
    val playerWidth: Int,
    val playerHeight: Int,

    val turnLocation: Point,
    val hitLocation: Point
)

fun GameBoardLayout(width: Int, height: Int, params: GameBoardLayoutParams)
    : GameBoardLayout {

    val numPlayerPiles = params.numPlayerPiles + 1 // nertz pile + cascade piles
    val cascadeMaxCards = Deck.CardsPerSuit

    // playerWidth1 = the max player card width that will fit the window width
    val playerWidthSansMargins = width -
        (2 * params.minSideMargin) -
        ((numPlayerPiles - 1) * params.betweenCardPadding)
    val playerWidth1 = playerWidthSansMargins / numPlayerPiles

    // playerWidth2 = the max player card width that will fit the window height, using the
    // given aspect ratio and ace pile parameters.
    val heightSansBetweenSize = height - params.betweenAcesAndPiles
    val aceHeightMultiplier = params.aceWidthFraction / params.aceAspectRatio
    val maxPlayerHeightFactor =  1 +
            ((cascadeMaxCards - 1) * params.cascadeOverlapFraction)
    val playerHeightMultiplier = maxPlayerHeightFactor / params.playerAspectRatio
    val playerWidth2 = heightSansBetweenSize / (aceHeightMultiplier + playerHeightMultiplier)

    // cardWidth = the min of the two cardWidths and the given max width
    val playerWidth = Math.min(Math.min(playerWidth1, playerWidth2), params.playerMaxWidth)
    val playerHeight = playerWidth / params.playerAspectRatio
    val playerSideMargin = (params.minSideMargin +
        ((playerWidthSansMargins - (numPlayerPiles * playerWidth)) / 2)).toInt()

    val numAcePiles = Suit.values().size
    val aceWidthSansMargin = width -
        (2 * params.minSideMargin) -
            ((numAcePiles - 1) * params.betweenCardPadding)
    val aceWidth = playerWidth * params.aceWidthFraction
    val aceHeight = aceWidth / params.aceAspectRatio
    val aceSideMargin = (params.minSideMargin +
            ((aceWidthSansMargin - (numAcePiles * aceWidth)) / 2)).toInt()

    val aceTop = 0
    val aceWidthPlusPadding = aceWidth + params.betweenCardPadding
    val aceLocations = Array<Point>(numAcePiles) {
        Point(aceSideMargin + (it * aceWidthPlusPadding).toInt(), aceTop)
    }

    val playerTop = (aceTop + aceHeight + params.betweenAcesAndPiles).toInt()
    val nertzLocation = Point(playerSideMargin, playerTop)

    val cascadeWidthPlusPadding = playerWidth + params.betweenCardPadding
    val cascadeLocations = Array<Point>(params.numPlayerPiles) {
        Point(playerSideMargin + ((it + 1) * cascadeWidthPlusPadding).toInt(), playerTop)
    }

    // TODO: Need param for distance between nertz pile and draw pile
    val turnLocation = Point(nertzLocation.x,
            (nertzLocation.y + playerHeight + params.betweenAcesAndPiles).toInt())
    val hitLocation = Point(turnLocation.x,
            (turnLocation.y + playerHeight + params.betweenAcesAndPiles).toInt())

    return GameBoardLayout(
        aceLocations,
        aceWidth.toInt(),
        aceHeight.toInt(),
        playerTop,
        nertzLocation,
        cascadeLocations,
        (playerHeight * params.cascadeOverlapFraction).toInt(),
        playerWidth.toInt(),
        playerHeight.toInt(),
        turnLocation,
        hitLocation)
}