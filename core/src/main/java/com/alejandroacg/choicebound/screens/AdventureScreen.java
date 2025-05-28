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
    private Table contentTable;
    private ScrollPane scrollPane;
    private final String initialNodeId;

    public AdventureScreen(ChoiceboundGame game, LocalAdventure adventure, String nodeId) {
        this.game = game;
        this.adventure = adventure;
        this.initialNodeId = nodeId;
        this.stage = new Stage(new ScreenViewport());
        this.uiElementFactory = new UIElementFactory(game.getResourceManager(), game.getSkin());
        this.backgroundColor = game.getSkin().getColor("parchment_light");
        Gdx.input.setInputProcessor(stage);
        setupUI();
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

        float labelSize = 1.4f;

        Label heroLabel = new Label(GameConfig.getString("hero") + ": "  + (progress != null ? progress.getCurrentHero() : 0), game.getSkin(), "roleplay_narrative_orange");
        heroLabel.setFontScale(labelSize);
        heroLabel.setAlignment(Align.center);

        Label cowardLabel = new Label(GameConfig.getString("coward") + ": "  + (progress != null ? progress.getCurrentCoward() : 0), game.getSkin(), "roleplay_narrative_purple");
        cowardLabel.setFontScale(labelSize);
        cowardLabel.setAlignment(Align.center);

        Label killerLabel = new Label(GameConfig.getString("killer") + ": "  + (progress != null ? progress.getCurrentKiller() : 0), game.getSkin(), "roleplay_narrative_red");
        killerLabel.setFontScale(labelSize);
        killerLabel.setAlignment(Align.center);

        TextButton backButton = uiElementFactory.createDefaultButton(GameConfig.getString("back"));
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getOverlayManager().showLoadingOverlay(stage);
                game.getResourceManager().unloadAdventureArt(adventure.getUid());
                game.setScreen(new HomeScreen(game));
            }
        });

        Table statsTable = new Table();
        statsTable.add(heroLabel).padRight(30f);
        statsTable.add(cowardLabel).padRight(30f);
        statsTable.add(killerLabel);

        Table headerContent = new Table();
        headerContent.setFillParent(true);
        headerContent.top().center();
        headerContent.add(titleLabel).padTop(20).padBottom(-20f).row();
        headerContent.add(statsTable).padBottom(5f).row();
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
        image.setScaling(Scaling.fillX);
        image.setAlign(Align.center);
        mainTable.add(image)
            .expandX()
            .fillX()
            .height(screenHeight * 0.3f)
            .padTop(0)
            .padLeft(0)
            .padRight(0)
            .row();

        contentTable = new Table();
        scrollPane = new ScrollPane(contentTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        Table scrollWrapper = new Table();
        scrollWrapper.add(scrollPane).expand().fill();

        Container<Table> parchmentContainer = new Container<>(scrollWrapper);
        parchmentContainer.setBackground(new TextureRegionDrawable(game.getResourceManager().getAtlas("ui").findRegion("container_parchment")));
        parchmentContainer.padTop(150f).padBottom(200f).padLeft(0f).padRight(0f);

        mainTable.add(parchmentContainer).width(Gdx.graphics.getWidth() * 0.9f).padTop(20f).row();
    }

    private void loadNode(String nodeId) {
        if (!game.getConnectivityChecker().checkConnectivityWithRedirect()) {
            return;
        }

        Group overlay = game.getOverlayManager().showLoadingOverlay(stage);
        game.getDataManager().loadNode(
            adventure.getUid(),
            nodeId,
            node -> {
                currentNode = node;
                Gdx.app.log("AdventureScreen", "Nodo cargado: " + nodeId);
                game.getOverlayManager().hideOverlay(overlay);

                Gdx.app.log("AdventureScreen", "Intentando cargar imagen: " + currentNode.getImage());
                TextureRegion imageRegion = game.getResourceManager().getAtlas(adventure.getUid() + "_art").findRegion(currentNode.getImage());
                if (imageRegion != null) {
                    Gdx.app.log("AdventureScreen", "Imagen cargada correctamente: " + currentNode.getImage());
                    Gdx.app.log("AdventureScreen", "Dimensiones de la región: " + imageRegion.getRegionWidth() + "x" + imageRegion.getRegionHeight());
                    image.setDrawable(new TextureRegionDrawable(imageRegion));
                } else {
                    Gdx.app.error("AdventureScreen", "Imagen no encontrada para el nodo: " + currentNode.getImage());
                    image.setDrawable(null);
                }

                if (currentNode.getMusic() != null) {
                    game.getMusicManager().playIfDifferent(currentNode.getMusic());
                }

                Table newContentTable = new Table();
                Label descriptionLabel = new Label(currentNode.getText(), game.getSkin(), "roleplay_narrative_light_grey");
                descriptionLabel.setFontScale(1.1f);
                descriptionLabel.setWrap(true);
                descriptionLabel.setAlignment(Align.center);
                newContentTable.add(descriptionLabel).expandX().fillX().padBottom(40f).row();

                for (LocalNode.LocalChoice choice : currentNode.getChoices()) {
                    TextButton choiceButton = uiElementFactory.createDefaultButton(choice.getText());
                    choiceButton.getLabel().setWrap(true);
                    choiceButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            if (game.getConnectivityChecker().checkConnectivity(stage)) {
                                Group overlay = game.getOverlayManager().showLoadingOverlay(stage);

                                LocalUser.LocalProgress progress = game.getLocalUser().getProgress().get(adventure.getUid());
                                if (progress != null) {
                                    if (choice.getModifierHero() != null) {
                                        progress.setCurrentHero(progress.getCurrentHero() + choice.getModifierHero());
                                    }
                                    if (choice.getModifierCoward() != null) {
                                        progress.setCurrentCoward(progress.getCurrentCoward() + choice.getModifierCoward());
                                    }
                                    if (choice.getModifierKiller() != null) {
                                        progress.setCurrentKiller(progress.getCurrentKiller() + choice.getModifierKiller());
                                    }
                                    progress.setCurrentNode(choice.getNextNodeId());
                                }

                                game.getDataManager().saveUserData(
                                    game.getLocalUser(),
                                    () -> {
                                        Gdx.app.log("AdventureScreen", "Usuario guardado en Firestore tras elegir opción");
                                        game.getOverlayManager().hideOverlay(overlay);
                                        loadNode(choice.getNextNodeId());
                                    },
                                    error -> {
                                        Gdx.app.error("AdventureScreen", "Error al guardar usuario: " + error);
                                        game.getOverlayManager().hideOverlay(overlay);
                                        game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
                                    }
                                );
                            }
                        }
                    });
                    newContentTable.add(choiceButton).expandX().fillX().width(Gdx.graphics.getWidth() * 0.8f).padBottom(10f).row();
                }

                // Cambiar el contenido del ScrollPane de forma segura
                Gdx.app.postRunnable(() -> {
                    scrollPane.setActor(newContentTable);
                    contentTable = newContentTable;
                });
            },
            error -> {
                Gdx.app.error("AdventureScreen", "Error al cargar nodo: " + error);
                game.getOverlayManager().hideOverlay(overlay);
                game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
            }
        );
    }

    @Override
    public void show() {
        loadNode(initialNodeId);
    }

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
