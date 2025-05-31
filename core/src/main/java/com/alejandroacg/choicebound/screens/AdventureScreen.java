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

import java.util.HashSet;

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
    private Image alignmentIconImage;

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

        float alignmentIconImageSize = 150f;

        alignmentIconImage = new Image();
        alignmentIconImage.setScaling(Scaling.fit);
        alignmentIconImage.setSize(alignmentIconImageSize, alignmentIconImageSize);
        alignmentIconImage.setAlign(Align.center);

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
        statsTable.add(alignmentIconImage).size(alignmentIconImageSize, alignmentIconImageSize);

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

        if ("node000".equals(nodeId)) {
            LocalUser.LocalProgress progress = game.getLocalUser().getProgress().get(adventure.getUid());
            if (progress != null) {
                progress.setCurrentHero(adventure.getInitialHero());
                progress.setCurrentCoward(adventure.getInitialCoward());
                progress.setCurrentKiller(adventure.getInitialKiller());
                progress.setCurrentNode("node000");
                progress.setTriggers(new HashSet<>());

                game.getDataManager().saveUserData(
                    game.getLocalUser(),
                    () -> {
                        Gdx.app.log("AdventureScreen", "Usuario guardado en Firestore al iniciar nueva aventura");
                    },
                    error -> {
                        Gdx.app.error("AdventureScreen", "Error al guardar usuario al iniciar aventura: " + error);
                        game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
                    }
                );
            }
        }

        game.getOverlayManager().showLoadingOverlay(stage);
        game.getDataManager().loadNode(
            adventure.getUid(),
            nodeId,
            node -> {
                currentNode = node;
                Gdx.app.log("AdventureScreen", "Nodo cargado: " + nodeId);
                game.getOverlayManager().hideLoadingOverlay();

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
                newContentTable.setWidth(Gdx.graphics.getWidth() * 0.8f);
                Label descriptionLabel = new Label(currentNode.getText(), game.getSkin(), "roleplay_narrative_light_grey");
                descriptionLabel.setFontScale(1.1f);
                descriptionLabel.setWrap(true);
                descriptionLabel.setAlignment(Align.center);
                newContentTable.add(descriptionLabel).expandX().fillX().minHeight(100f).padBottom(40f).row();

                LocalUser.LocalProgress progress = game.getLocalUser().getProgress().get(adventure.getUid());
                if (progress != null) {
                    int hero = progress.getCurrentHero();
                    int coward = progress.getCurrentCoward();
                    int killer = progress.getCurrentKiller();

                    String dominantRole;
                    if (hero > coward && hero > killer) {
                        dominantRole = "hero";
                    } else if (coward > hero && coward > killer) {
                        dominantRole = "coward";
                    } else if (killer > hero && killer > coward) {
                        dominantRole = "killer";
                    } else {
                        dominantRole = "neutral";
                    }

                    TextureRegion roleIconRegion = game.getResourceManager().getAtlas("adventure0_art").findRegion("adventure0_" + dominantRole + "_icon");
                    if (roleIconRegion != null) {
                        alignmentIconImage.setDrawable(new TextureRegionDrawable(roleIconRegion));
                    } else {
                        Gdx.app.error("AdventureScreen", "Textura no encontrada: adventure0_" + dominantRole + "_icon");
                        alignmentIconImage.setDrawable(null);
                    }
                } else {
                    Gdx.app.error("AdventureScreen", "Progreso nulo para aventura: " + adventure.getUid());
                    alignmentIconImage.setDrawable(null);
                }

                for (LocalNode.LocalChoice choice : currentNode.getChoices()) {
                    boolean showChoice = true;

                    if (choice.getConditionHero() != null && progress != null) {
                        if (progress.getCurrentHero() < choice.getConditionHero()) {
                            showChoice = false;
                        }
                    }
                    if (choice.getConditionCoward() != null && progress != null) {
                        if (progress.getCurrentCoward() < choice.getConditionCoward()) {
                            showChoice = false;
                        }
                    }
                    if (choice.getConditionKiller() != null && progress != null) {
                        if (progress.getCurrentKiller() < choice.getConditionKiller()) {
                            showChoice = false;
                        }
                    }

                    if (choice.getConditionTriggersPositive() != null && !choice.getConditionTriggersPositive().isEmpty() && progress != null) {
                        for (String trigger : choice.getConditionTriggersPositive()) {
                            if (!progress.getTriggers().contains(trigger)) {
                                showChoice = false;
                                break;
                            }
                        }
                    }

                    if (choice.getConditionTriggersNegative() != null && !choice.getConditionTriggersNegative().isEmpty() && progress != null) {
                        for (String trigger : choice.getConditionTriggersNegative()) {
                            if (progress.getTriggers().contains(trigger)) {
                                showChoice = false;
                                break;
                            }
                        }
                    }

                    if (showChoice) {
                        TextButton choiceButton = uiElementFactory.createDefaultButton(choice.getText());
                        choiceButton.getLabel().setWrap(true);
                        choiceButton.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                if (game.getConnectivityChecker().checkConnectivity(stage)) {
                                    game.getOverlayManager().showLoadingOverlay(stage);

                                    String nextNodeId = choice.getNextNodeId() == null ? "node_wip" : choice.getNextNodeId();
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
                                        if (choice.getTriggerToSet() != null && !choice.getTriggerToSet().isEmpty()) {
                                            progress.getTriggers().addAll(choice.getTriggerToSet());
                                        }
                                        if (choice.getTriggerToRemove() != null && !choice.getTriggerToRemove().isEmpty()) {
                                            progress.getTriggers().removeAll(choice.getTriggerToRemove());
                                        }
                                        progress.setCurrentNode(nextNodeId);
                                    }

                                    game.getDataManager().saveUserData(
                                        game.getLocalUser(),
                                        () -> {
                                            Gdx.app.log("AdventureScreen", "Usuario guardado en Firestore tras elegir opción");
                                            game.getOverlayManager().hideLoadingOverlay();
                                            loadNode(nextNodeId);
                                        },
                                        error -> {
                                            Gdx.app.error("AdventureScreen", "Error al guardar usuario: " + error);
                                            game.getOverlayManager().hideLoadingOverlay();
                                            game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
                                        }
                                    );
                                }
                            }
                        });
                        newContentTable.add(choiceButton).expandX().fillX().width(Gdx.graphics.getWidth() * 0.8f).padBottom(10f).row();
                    }
                }

                Gdx.app.postRunnable(() -> {
                    scrollPane.setActor(newContentTable);
                    contentTable = newContentTable;
                });
            },
            error -> {
                Gdx.app.error("AdventureScreen", "Error al cargar nodo: " + error);
                game.getOverlayManager().hideLoadingOverlay();
                game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));

                if (!"node_wip".equals(nodeId)) {
                    Gdx.app.log("AdventureScreen", "Intentando cargar nodo de reemplazo: node_wip");
                    loadNode("node_wip");
                } else {
                    game.getOverlayManager().showMessageOverlay(stage, GameConfig.getString("error_message"));
                }
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
