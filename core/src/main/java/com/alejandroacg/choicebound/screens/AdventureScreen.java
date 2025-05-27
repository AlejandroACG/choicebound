package com.alejandroacg.choicebound.screens;

import com.alejandroacg.choicebound.ChoiceboundGame;
import com.alejandroacg.choicebound.data.LocalAdventure;
import com.alejandroacg.choicebound.data.LocalNode;
import com.alejandroacg.choicebound.data.LocalUser;
import com.alejandroacg.choicebound.ui.UIElementFactory;
import com.alejandroacg.choicebound.utils.GameConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class AdventureScreen implements Screen {
    private final ChoiceboundGame game;
    private final Stage stage;
    private final UIElementFactory uiElementFactory;
    private final Color backgroundColor;
    private final LocalAdventure adventure;
    private LocalNode currentNode;
    private Image image;
    private Label descriptionLabel;
    private Table contentTable;
    private ScrollPane scrollPane; // Para poder actualizar el contenido

    public AdventureScreen(ChoiceboundGame game, LocalAdventure adventure, String nodeId) {
        this.game = game;
        this.adventure = adventure;
        this.stage = new Stage(new ScreenViewport());
        this.uiElementFactory = new UIElementFactory(game.getResourceManager(), game.getSkin());
        this.backgroundColor = game.getSkin().getColor("parchment_light");
        Gdx.input.setInputProcessor(stage);
        setupUI();

        loadNode(nodeId);
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        stage.addActor(mainTable);

        float screenHeight = Gdx.graphics.getHeight();
        float titleScale = 3f;

        Table header = uiElementFactory.createHeader();

        Label titleLabel = uiElementFactory.createTitleLabel(adventure.getTitle());
        titleLabel.setFontScale(titleScale);
        titleLabel.setAlignment(Align.center);

        LocalUser.LocalProgress progress = game.getLocalUser().getProgress() != null ?
            game.getLocalUser().getProgress().get(adventure.getUid()) : null;

        Label livesLabel = new Label("Lives: " + (progress != null ? progress.getCurrentLives() : 0), game.getSkin(), "roleplay_narrative_grey");
        livesLabel.setAlignment(Align.center);

        Label heroLabel = new Label("Hero: " + (progress != null ? progress.getCurrentHero() : 0), game.getSkin(), "roleplay_narrative_grey");
        heroLabel.setAlignment(Align.center);

        Label cowardLabel = new Label("Coward: " + (progress != null ? progress.getCurrentCoward() : 0), game.getSkin(), "roleplay_narrative_grey");
        cowardLabel.setAlignment(Align.center);

        Label killerLabel = new Label("Killer: " + (progress != null ? progress.getCurrentKiller() : 0), game.getSkin(), "roleplay_narrative_grey");
        killerLabel.setAlignment(Align.center);

        TextButton backButton = uiElementFactory.createDefaultButton("Back");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Group overlay = game.getOverlayManager().showOverlay(stage);
                game.getResourceManager().unloadAdventureArt(adventure.getUid());
                game.setScreen(new HomeScreen(game));
            }
        });

        Table headerContent = new Table();
        headerContent.setFillParent(true);
        headerContent.top().center();
        headerContent.add(titleLabel).padTop(20).padBottom(-20f).row();
        headerContent.add(livesLabel).padBottom(5f).row();
        headerContent.add(heroLabel).padBottom(5f).row();
        headerContent.add(cowardLabel).padBottom(5f).row();
        headerContent.add(killerLabel).padBottom(5f).row();
        headerContent.add(backButton).padTop(10).padBottom(30);

        header.addActor(headerContent);

        mainTable.add(header)
            .height(screenHeight * GameConfig.HEADER_HEIGHT_RATIO)
            .width(Gdx.graphics.getWidth())
            .expandX()
            .fillX()
            .padTop(0)
            .row();

        image = new Image();
        image.setScaling(Scaling.fit);
        image.setAlign(Align.center);
        mainTable.add(image)
            .expandX()
            .fillX()
            .padTop(0)
            .padLeft(0)
            .padRight(0)
            .row();

        descriptionLabel = new Label("Adventure description goes here.", game.getSkin(), "roleplay_narrative_grey");
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.topLeft);

        TextButton actionButton = uiElementFactory.createDefaultButton("Start Adventure");
        actionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Start adventure logic
            }
        });

        contentTable = new Table();
        contentTable.add(descriptionLabel).expandX().fillX().padBottom(10f).row();
        contentTable.add(actionButton).center();

        scrollPane = new ScrollPane(contentTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        Table scrollWrapper = new Table();
        scrollWrapper.add(scrollPane).expand().fill();

        Container<Table> parchmentContainer = new Container<>(scrollWrapper);
        parchmentContainer.setBackground(new TextureRegionDrawable(game.getResourceManager().getAtlas("ui").findRegion("container_parchment")));
        parchmentContainer.padTop(150f).padBottom(150f).padLeft(50f).padRight(50f);

        mainTable.add(parchmentContainer).width(Gdx.graphics.getWidth() * 0.9f).pad(20f).row();
    }

    private void loadNode(String nodeId) {
        if (game.getConnectivityChecker().checkConnectivity(stage)) {
            Group overlay = game.getOverlayManager().showOverlay(stage);
            game.getDataManager().loadNode(
                adventure.getUid(),
                nodeId,
                node -> {
                    currentNode = node;
                    Gdx.app.log("AdventureScreen", "Nodo cargado: " + nodeId);
                    game.getOverlayManager().hideOverlay(overlay);

                    LocalUser.LocalProgress progress = game.getLocalUser().getProgress().get(adventure.getUid());
                    if (progress != null) {
                        progress.setCurrentNode(currentNode.getUid());
                    }

                    Gdx.app.log("AdventureScreen", "Intentando cargar imagen: " + currentNode.getImage());
                    TextureRegion imageRegion = game.getResourceManager().getAtlas(adventure.getUid() + "_art").findRegion(currentNode.getImage());
                    if (imageRegion != null) {
                        image.setDrawable(new TextureRegionDrawable(imageRegion));
                    } else {
                        Gdx.app.error("AdventureScreen", "Imagen no encontrada para el nodo: " + currentNode.getImage());
                        image.setDrawable(null);
                    }

                    if (currentNode.getMusic() != null) {
                        game.getMusicManager().playIfDifferent(currentNode.getMusic());
                    }

                    descriptionLabel.setText(currentNode.getText());

                    // Crear un nuevo Table para evitar problemas con el estado interno
                    Table newContentTable = new Table();
                    newContentTable.add(descriptionLabel).expandX().fillX().padBottom(10f).row();

                    for (LocalNode.LocalChoice choice : currentNode.getChoices()) {
                        TextButton choiceButton = uiElementFactory.createDefaultButton(choice.getText());
                        choiceButton.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                if (game.getConnectivityChecker().checkConnectivity(stage)) {
                                    Group overlay = game.getOverlayManager().showOverlay(stage);

                                    LocalUser.LocalProgress progress = game.getLocalUser().getProgress().get(adventure.getUid());
                                    if (progress != null) {
                                        if (choice.getModifierLife() != null) {
                                            progress.setCurrentLives(progress.getCurrentLives() + choice.getModifierLife());
                                        }
                                        if (choice.getModifierHero() != null) {
                                            progress.setCurrentHero(progress.getCurrentHero() + choice.getModifierHero());
                                        }
                                        if (choice.getModifierCoward() != null) {
                                            progress.setCurrentCoward(progress.getCurrentCoward() + choice.getModifierCoward());
                                        }
                                        if (choice.getModifierKiller() != null) {
                                            progress.setCurrentKiller(progress.getCurrentKiller() + choice.getModifierKiller());
                                        }
                                    }

                                    game.getDataManager().saveUserData(
                                        game.getLocalUser(),
                                        () -> {
                                            Gdx.app.log("AdventureScreen", "Usuario guardado en Firestore tras elegir opción");
                                            loadNode(choice.getNextNodeId());
                                        },
                                        error -> {
                                            Gdx.app.error("AdventureScreen", "Error al guardar usuario: " + error);
                                            game.getOverlayManager().hideOverlay(overlay);
                                            game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
                                        }
                                    );
                                } else {
                                    game.getOverlayManager().hideOverlay(game.getOverlayManager().showOverlay(stage));
                                    game.setScreen(new HomeScreen(game));
                                }
                            }
                        });
                        newContentTable.add(choiceButton).expandX().fillX().padBottom(10f).row();
                    }

                    // Reemplazar el contenido del ScrollPane
                    scrollPane.setActor(newContentTable);
                    contentTable = newContentTable; // Actualizar la referencia
                },
                error -> {
                    Gdx.app.error("AdventureScreen", "Error al cargar nodo: " + error);
                    game.getOverlayManager().hideOverlay(overlay);
                    game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
                }
            );
        } else {
            game.setScreen(new HomeScreen(game));
        }
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
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
