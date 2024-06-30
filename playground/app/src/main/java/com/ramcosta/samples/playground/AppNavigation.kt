package com.ramcosta.samples.playground

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.bottomsheet.utils.bottomSheetComposable
import com.ramcosta.composedestinations.generated.featurey.navtype.internalBackResultNavType
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCallsBuilder
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navargs.primitives.DestinationsBooleanNavType
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.destination
import com.ramcosta.composedestinations.navigation.getBackStackEntry
import com.ramcosta.composedestinations.navigation.navGraph
import com.ramcosta.composedestinations.scope.resultBackNavigator
import com.ramcosta.composedestinations.scope.resultRecipient
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.utils.composable
import com.ramcosta.composedestinations.utils.dialogComposable
import com.ramcosta.samples.playground.commons.DrawerController
import com.ramcosta.samples.playground.di.viewModel
import com.ramcosta.samples.playground.ui.screens.Feed
import com.ramcosta.samples.playground.ui.screens.GoToProfileConfirmation
import com.ramcosta.samples.playground.ui.screens.NavGraphs
import com.ramcosta.samples.playground.ui.screens.TestScreen
import com.ramcosta.samples.playground.ui.screens.destinations.FeedDestination
import com.ramcosta.samples.playground.ui.screens.destinations.GoToProfileConfirmationDestination
import com.ramcosta.samples.playground.ui.screens.destinations.GreetingScreenDestination
import com.ramcosta.samples.playground.ui.screens.destinations.ProfileScreenDestination
import com.ramcosta.samples.playground.ui.screens.destinations.SettingsScreenDestination
import com.ramcosta.samples.playground.ui.screens.destinations.TestScreenDestination
import com.ramcosta.samples.playground.ui.screens.destinations.ThemeSettingsDestination
import com.ramcosta.samples.playground.ui.screens.greeting.GreetingScreen
import com.ramcosta.samples.playground.ui.screens.greeting.GreetingUiEvents
import com.ramcosta.samples.playground.ui.screens.greeting.GreetingUiState
import com.ramcosta.samples.playground.ui.screens.greeting.GreetingViewModel
import com.ramcosta.samples.playground.ui.screens.navtype.serializableExampleWithNavTypeSerializerNavType
import com.ramcosta.samples.playground.ui.screens.profile.ProfileScreen
import com.ramcosta.samples.playground.ui.screens.profile.ProfileUiEvents
import com.ramcosta.samples.playground.ui.screens.profile.ProfileUiState
import com.ramcosta.samples.playground.ui.screens.profile.ProfileViewModel
import com.ramcosta.samples.playground.ui.screens.settings.SettingsScreen
import com.ramcosta.samples.playground.ui.screens.settings.SettingsViewModel
import com.ramcosta.samples.playground.ui.screens.settings.ThemeSettings

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    drawerController: DrawerController,
    navController: NavHostController,
    testProfileDeepLink: () -> Unit,
) {
    DestinationsNavHost(
        navGraph = NavGraphs.root,
        startRoute = if (Math.random() > 0.5) FeedDestination else NavGraphs.root.startRoute,
        navController = navController,
        modifier = modifier,
        dependenciesContainerBuilder = {
            dependency(drawerController)

            destination(ProfileScreenDestination) {
                dependency(viewModel<ProfileViewModel>())
            }

            navGraph(NavGraphs.settings) {
                val parentEntry = remember(navBackStackEntry) {
                    navController.getBackStackEntry(NavGraphs.settings)
                }

                dependency(viewModel<SettingsViewModel>(parentEntry))
            }
        }
    ) {
        TestScreenDestination addDeepLink { uriPattern = "runtimeschema://${TestScreenDestination.route}" }

        ProfileScreenDestination animateWith object: DestinationStyle.Animated() {
            override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
                { fadeIn(tween(5000)) }
            override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
                { fadeOut(tween(2000)) }
        }

        greetingScreen(testProfileDeepLink, drawerController)
    }
}

private fun ManualComposableCallsBuilder.greetingScreen(
    testProfileDeepLink: () -> Unit,
    drawerController: DrawerController
) {
    composable(GreetingScreenDestination) {
        val vm = viewModel<GreetingViewModel>()

        GreetingScreen(
            navigator = destinationsNavigator,
            testProfileDeepLink = testProfileDeepLink,
            drawerController = drawerController,
            uiEvents = vm as GreetingUiEvents,
            uiState = vm as GreetingUiState,
            test = "testing param from NavHost",
            resultRecipient = resultRecipient(DestinationsBooleanNavType),
            featYResult = resultRecipient(internalBackResultNavType),
        )
    }
}

// region ------- Without using DestinationsNavHost example -------
@Suppress("UNUSED")
@ExperimentalMaterialNavigationApi
@ExperimentalAnimationApi
@Composable
fun SampleAppAnimatedNavHostExample(
    modifier: Modifier,
    navController: NavHostController,
    drawerController: DrawerController,
    testProfileDeepLink: () -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = GreetingScreenDestination.route,
        route = "root"
    ) {

        composable(GreetingScreenDestination) {
            val vm = viewModel<GreetingViewModel>()

            GreetingScreen(
                navigator = destinationsNavigator(navController),
                drawerController = drawerController,
                uiEvents = vm as GreetingUiEvents,
                uiState = vm as GreetingUiState,
                resultRecipient = resultRecipient(DestinationsBooleanNavType),
                featYResult = resultRecipient(internalBackResultNavType),
                testProfileDeepLink = testProfileDeepLink,
                test = "testing param from NavHost",
            )
        }

        composable(FeedDestination) {
            Feed(destinationsNavigator(navController), resultRecipient(internalBackResultNavType))
        }

        dialogComposable(GoToProfileConfirmationDestination) {
            GoToProfileConfirmation(
                resultNavigator = resultBackNavigator(navController, DestinationsBooleanNavType)
            )
        }

        composable(TestScreenDestination) {
            TestScreen(
                id = navArgs.id,
                asd = navArgs.asd,
                stuff1 = navArgs.stuff1,
                stuff2 = navArgs.stuff2,
                stuff3 = navArgs.stuff3,
                stuff5 = navArgs.stuff5,
                stuff6 = navArgs.stuff6,
            )
        }

        composable(ProfileScreenDestination) {
            val vm = viewModel<ProfileViewModel>()

            ProfileScreen(
                vm as ProfileUiState,
                vm as ProfileUiEvents,
            )
        }

        navigation(
            startDestination = SettingsScreenDestination.route,
            route = "settings"
        ) {
            composable(SettingsScreenDestination) {
                SettingsScreen(
                    viewModel = viewModel(),
                    navigator = destinationsNavigator(navController),
                    themeSettingsResultRecipient = resultRecipient(serializableExampleWithNavTypeSerializerNavType)
                )
            }

            bottomSheetComposable(ThemeSettingsDestination) {
                ThemeSettings(
                    viewModel = viewModel(),
                    resultNavigator = resultBackNavigator(navController, serializableExampleWithNavTypeSerializerNavType)
                )
            }
        }
    }
}
// endregion
