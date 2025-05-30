package com.alejandroacg.choicebound.screens;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.data.LocalAdventure;
import com.alejandroacg.choicebound.data.LocalUser;
import com.alejandroacg.choicebound.ui.UIElementFactory;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.alejandroacg.choicebound.utils.GameConfig.HEADER_HEIGHT_RATIO;

public class HomeScreen implements Screen {
    private final ChoiceboundGame game;
    private final Stage stage;
    private final UIElementFactory uiElementFactory;
    private final Color backgroundColor;
    private List<LocalAdventure> adventures;
    private Table scrollContent;
    private boolean awaitingAtlasLoad = false;
    private LocalAdventure pendingAdventure = null;
    private String pendingNodeId = null;


    public HomeScreen(ChoiceboundGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.uiElementFactory = new UIElementFactory(game.getResourceManager(), game.getSkin());
        this.adventures = new ArrayList<>();
        Gdx.input.setInputProcessor(stage);

        this.backgroundColor = game.getSkin().getColor("parchment_light");
        setupUI();
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        float screenHeight = Gdx.graphics.getHeight();
        float titleScale = 3f;
        float subtitleScale = 2f;

        Table header = uiElementFactory.createHeader();

        Label titleLabel = uiElementFactory.createTitleLabel(GameConfig.getString("title_choicebound"));
        titleLabel.setFontScale(titleScale);
        titleLabel.setAlignment(Align.center);

        Label welcomeLabel = new Label(
            GameConfig.getString("welcome_message") + ", " + game.getLocalUser().getUsername(),
            game.getSkin(),
            "roleplay_narrative_grey"
        );
        welcomeLabel.setFontScale(subtitleScale);
        welcomeLabel.setAlignment(Align.center);

        TextButton storeButton = uiElementFactory.createDefaultButton(GameConfig.getString("store_button"));
        storeButton.setDisabled(true);

        TextButton settingsButton = uiElementFactory.createDefaultButton(GameConfig.getString("settings"));
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SettingsScreen(game));
            }
        });

        TextButton refreshButton = uiElementFactory.createDefaultButton(GameConfig.getString("refresh"));
        refreshButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HomeScreen(game));
            }
        });

        Table buttonsTable = new Table();
        buttonsTable.add(storeButton).padRight(20);
        buttonsTable.add(settingsButton).padRight(20);
        buttonsTable.add(refreshButton);

        Table headerContent = new Table();
        headerContent.setFillParent(true);
        headerContent.top().center();
        headerContent.add(titleLabel).padTop(20).padBottom(-20f).row();
        headerContent.add(welcomeLabel).row();
        headerContent.add(buttonsTable).padTop(10).padBottom(30);

        header.addActor(headerContent);

        mainTable.add(header)
            .height(screenHeight * HEADER_HEIGHT_RATIO)
            .width(Gdx.graphics.getWidth())
            .expandX()
            .fillX()
            .row();

        scrollContent = new Table();
        scrollContent.top();

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        mainTable.add(scrollPane).expand().fill().row();
        mainTable.add().height(20).row();

        TextButton signOutButton = uiElementFactory.createDefaultButton(GameConfig.getString("sign_out"));
        signOutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.signOut();
            }
        });

        mainTable.add(signOutButton).padBottom(20).bottom();
    }

    private void startNewAdventure(LocalAdventure adventure) {
        game.getOverlayManager().showTempMessageOverlay(stage,
            "\n" + GameConfig.getString("loading") + " " + adventure.getTitle() + "...\n");
        game.getResourceManager().loadAdventureArt(adventure.getUid(), () -> {});
        awaitingAtlasLoad = true;
        pendingAdventure = adventure;
        pendingNodeId = "node000";
    }

    @Override
    public void show() {
        game.getMusicManager().playIfDifferent("main_menu");

        if (game.getConnectivityChecker().checkConnectivity(stage)) {
            game.getDataManager().loadAllAdventures(
                loadedAdventures -> {
                    adventures = loadedAdventures;
                    Gdx.app.log("HomeScreen", "Aventuras cargadas: " + adventures.size());
                    Gdx.app.postRunnable(() -> {
                        scrollContent.clear();
                        for (LocalAdventure adventure : adventures) {
                            boolean isUnlocked = false;
                            boolean hasProgress = false;
                            Map<String, LocalUser.LocalProgress> userProgress = game.getLocalUser().getProgress();
                            if (userProgress != null && userProgress.containsKey(adventure.getUid())) {
                                LocalUser.LocalProgress progress = userProgress.get(adventure.getUid());
                                isUnlocked = progress.isUnlocked();
                                hasProgress = progress.getCurrentNode() != null;
                            }

                            final boolean finalHasProgress = hasProgress;
                            final String currentNodeId = hasProgress ? userProgress.get(adventure.getUid()).getCurrentNode() : "node000";

                            String coverKey = isUnlocked ? adventure.getCover() : adventure.getCover() + "_locked";
                            Gdx.app.log("Adventure", "Intentando cargar la textura: " + coverKey);
                            TextureRegion coverRegion = game.getResourceManager().getAtlas("covers").findRegion(coverKey);
                            Image coverImage = new Image(coverRegion);
                            coverImage.setScaling(Scaling.fit);
                            coverImage.setAlign(Align.center);

                            Label titleLabel = uiElementFactory.createBoldTitleLabel(adventure.getTitle());
                            titleLabel.setAlignment(Align.center);
                            titleLabel.setWrap(true);
                            titleLabel.setFontScale(3f);

                            TextButton newAdventureButton = uiElementFactory.createDefaultButton(GameConfig.getString("new_adventure"));
                            TextButton continueAdventureButton = uiElementFactory.createDefaultButton(GameConfig.getString("continue_adventure"));

                            newAdventureButton.setDisabled(!isUnlocked);
                            continueAdventureButton.setDisabled(!isUnlocked || !hasProgress);

                            newAdventureButton.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    if (game.getConnectivityChecker().checkConnectivity(stage)) {
                                        if (finalHasProgress) {
                                            Dialog confirmationDialog = uiElementFactory.createConfirmationDialog(
                                                GameConfig.getString("confirm_new_adventure_message"),
                                                new UIElementFactory.ConfirmationListener() {
                                                    @Override
                                                    public void onConfirm() {
                                                        startNewAdventure(adventure);
                                                    }

                                                    @Override
                                                    public void onCancel() {
                                                        // No hacer nada si se cancela
                                                    }
                                                }
                                            );
                                            confirmationDialog.show(stage);
                                        } else {
                                            startNewAdventure(adventure);
                                        }
                                    }
                                }
                            });

                            continueAdventureButton.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    if (game.getConnectivityChecker().checkConnectivity(stage)) {
                                        game.getOverlayManager().showTempMessageOverlay(stage,
                                            "\n" + GameConfig.getString("loading") + " " + adventure.getTitle() + "...\n");
                                        game.getResourceManager().loadAdventureArt(adventure.getUid(), () -> {});
                                        awaitingAtlasLoad = true;
                                        pendingAdventure = adventure;
                                        pendingNodeId = currentNodeId;
                                    }
                                }
                            });

                            Table buttonsTable = new Table();
                            buttonsTable.add(newAdventureButton).padRight(20f);
                            buttonsTable.add(continueAdventureButton);

                            Table adventureTable = new Table();
                            adventureTable.add(coverImage).center().row();
                            adventureTable.add(titleLabel).expandX().fillX().padBottom(10f).row();
                            adventureTable.add(buttonsTable).center();

                            Container<Table> wrappedAdventure = new Container<>(adventureTable);
                            wrappedAdventure.setBackground(new TextureRegionDrawable(game.getResourceManager().getAtlas("ui").findRegion("container_parchment")));
                            wrappedAdventure.padTop(150f).padBottom(150f).padLeft(50f).padRight(50f);

                            scrollContent.add(wrappedAdventure).width(Gdx.graphics.getWidth() * 0.9f).pad(20f).row();
                        }
                    });
                },
                error -> {
                    Gdx.app.log("HomeScreen", "Error al cargar aventuras: " + error);
                    game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
                }
            );
        }
    }

    @Override
    public void render(float delta) {
        if (awaitingAtlasLoad && game.getResourceManager().update()) {
            awaitingAtlasLoad = false;
            game.setScreen(new AdventureScreen(game, pendingAdventure, pendingNodeId));
            return;
        }

        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
