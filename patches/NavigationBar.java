package ca.mcgill.sel.ram.ui.components.navigationbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.mt4j.components.TransformSpace;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.sceneManagement.Iscene;
import org.mt4j.sceneManagement.transition.BlendTransition;
import org.mt4j.sceneManagement.transition.SlideTransition;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import ca.mcgill.sel.classdiagram.ui.scenes.DisplayClassDiagramScene;
import ca.mcgill.sel.commons.emf.util.EMFEditUtil;
import ca.mcgill.sel.commons.emf.util.EMFModelUtil;
import ca.mcgill.sel.core.COREArtefact;
import ca.mcgill.sel.core.COREConcern;
import ca.mcgill.sel.core.COREExternalArtefact;
import ca.mcgill.sel.core.COREFeature;
import ca.mcgill.sel.core.COREFeatureImpactNode;
import ca.mcgill.sel.core.COREImpactNode;
import ca.mcgill.sel.core.COREModelComposition;
import ca.mcgill.sel.core.COREModelReuse;
import ca.mcgill.sel.core.COREPerspective;
import ca.mcgill.sel.core.COREReuse;
import ca.mcgill.sel.core.COREScene;
import ca.mcgill.sel.core.CorePackage;
import ca.mcgill.sel.core.impl.COREConcernImpl;
import ca.mcgill.sel.core.impl.COREFeatureImpl;
import ca.mcgill.sel.core.impl.COREImpactNodeImpl;
import ca.mcgill.sel.core.util.COREArtefactUtil;
import ca.mcgill.sel.core.util.COREModelUtil;
import ca.mcgill.sel.ram.Aspect;
import ca.mcgill.sel.ram.AspectMessageView;
import ca.mcgill.sel.ram.Classifier;
import ca.mcgill.sel.ram.MessageView;
import ca.mcgill.sel.ram.MessageViewReference;
import ca.mcgill.sel.ram.Operation;
import ca.mcgill.sel.ram.RAMPartialityType;
import ca.mcgill.sel.ram.RamPackage;
import ca.mcgill.sel.ram.StateView;
import ca.mcgill.sel.ram.impl.AspectImpl;
import ca.mcgill.sel.ram.impl.AspectMessageViewImpl;
import ca.mcgill.sel.ram.impl.MessageViewImpl;
import ca.mcgill.sel.ram.impl.MessageViewReferenceImpl;
import ca.mcgill.sel.ram.impl.OperationImpl;
import ca.mcgill.sel.ram.impl.StateViewImpl;
import ca.mcgill.sel.ram.impl.StructuralViewImpl;
import ca.mcgill.sel.ram.ui.RamApp;
import ca.mcgill.sel.ram.ui.components.RamButton;
import ca.mcgill.sel.ram.ui.components.RamImageComponent;
import ca.mcgill.sel.ram.ui.components.RamListComponent;
import ca.mcgill.sel.ram.ui.components.RamListComponent.Filter;
import ca.mcgill.sel.ram.ui.components.RamRectangleComponent;
import ca.mcgill.sel.ram.ui.components.RamRoundedRectangleComponent;
import ca.mcgill.sel.ram.ui.components.RamScrollComponent;
import ca.mcgill.sel.ram.ui.components.listeners.AbstractDefaultListListener;
import ca.mcgill.sel.ram.ui.components.navigationbar.NavigationBarMenu.NavigatonBarNamer;
import ca.mcgill.sel.ram.ui.components.navigationbar.NavigationBarMenu.NavigatonBarNamerRM;
import ca.mcgill.sel.ram.ui.events.listeners.ActionListener;
import ca.mcgill.sel.ram.ui.events.listeners.ITapAndHoldListener;
import ca.mcgill.sel.ram.ui.events.listeners.ITapListener;
import ca.mcgill.sel.ram.ui.layouts.DefaultLayout;
import ca.mcgill.sel.ram.ui.layouts.HorizontalLayoutVerticallyCentered;
import ca.mcgill.sel.ram.ui.layouts.VerticalLayout;
import ca.mcgill.sel.ram.ui.scenes.AbstractImpactScene;
import ca.mcgill.sel.ram.ui.scenes.DisplayAspectScene;
import ca.mcgill.sel.ram.ui.scenes.DisplayConcernEditScene;
import ca.mcgill.sel.ram.ui.scenes.DisplayConcernSelectScene;
import ca.mcgill.sel.ram.ui.scenes.DisplayImpactModelEditScene;
import ca.mcgill.sel.ram.ui.scenes.DisplayImpactModelSelectScene;
import ca.mcgill.sel.ram.ui.scenes.RamAbstractScene;
import ca.mcgill.sel.ram.ui.scenes.SelectAspectScene;
import ca.mcgill.sel.ram.ui.scenes.handler.impl.ConcernEditSceneHandler;
import ca.mcgill.sel.ram.ui.scenes.handler.impl.ConcernSelectSceneHandler;
import ca.mcgill.sel.ram.ui.scenes.handler.impl.DisplayAspectSceneHandler;
import ca.mcgill.sel.ram.ui.utils.Colors;
import ca.mcgill.sel.ram.ui.utils.GUIConstants;
import ca.mcgill.sel.ram.ui.utils.Icons;
import ca.mcgill.sel.ram.ui.utils.Strings;
import ca.mcgill.sel.ram.ui.views.handler.BaseHandler;
import ca.mcgill.sel.ram.ui.views.message.MessageViewView;
import ca.mcgill.sel.ram.ui.views.state.StateDiagramView;
import ca.mcgill.sel.ram.ui.views.structural.ClassifierView;
import ca.mcgill.sel.ram.ui.views.structural.CompositionSplitEditingView;
import ca.mcgill.sel.ram.ui.views.structural.StructuralDiagramView;
import ca.mcgill.sel.ram.util.Constants;
import ca.mcgill.sel.ram.util.MessageViewUtil;
import processing.core.PImage;

/**
 * A navigation bar showing the location of the user in the application and helps him to navigate into the app.
 * A back button is added to go back to the previous scene which is saved in a stack.
 * It's composed of a {@link NavigationBarSection} stack.
 *
 * @author Andrea
 */
public final class NavigationBar extends RamRectangleComponent {
    
    /**
     * Handler of the navigation bar attached to the back menu.
     * handles the single tap to pop sections and also the hold down to hide the bar
     * 
     * @author andrea
     */
    private final class BackMenuHandler extends BaseHandler implements ITapListener, ITapAndHoldListener {
    
        /**
         * Constructs the menu handler.
         */
        private BackMenuHandler() {
        }
    
        @Override
        public boolean processTapAndHoldEvent(TapAndHoldEvent tapAndHoldEvent) {
            if (tapAndHoldEvent.isHoldComplete()) {
                if (!(RamApp.getApplication().getCurrentScene() instanceof SelectAspectScene)) {
                    if (!historyOpen) {
                        if (!histories.isEmpty()) {
                            NavigationBarMenu menu = new NavigationBarMenu();
                            Vector3D menuPosition = backButton.getPosition(TransformSpace.GLOBAL);
                            menuPosition.x += backButton.getWidthXY(TransformSpace.GLOBAL) / 2.4;
                            menuPosition.y = BACK_BUTTON_SIZE - 25f;
    
                            List<RamAbstractScene<?>> list = new ArrayList<RamAbstractScene<?>>(histories);
                            menu.addMenuElement(true, list, new AbstractDefaultListListener<RamAbstractScene<?>>() {
    
                                @Override
                                public void elementSelected(RamListComponent<RamAbstractScene<?>> list,
                                        RamAbstractScene<?> element) {
    
                                    if (!element.equals(RamApp.getApplication().getCurrentScene())) {
                                        returnToSection(element);
                                        closeHistoryMenu();
                                        historyOpen = false;
                                    }
                                }
                                
                            }, null);
    
                            openHistoryMenu(menu, menuPosition);
                            historyOpen = true;
                        }
                    } else {
                        closeHistoryMenu();
                        historyOpen = false;
                    }
                }
            }
            return false;
        }
    
        @Override
        public boolean processTapEvent(TapEvent tapEvent) {
            if (tapEvent.isTapped() && isEnabled()) {
                closeMenu();
                if (RamApp.getApplication().getCurrentScene() instanceof DisplayConcernSelectScene) {
                    concernSelectMode();
                    ConcernSelectSceneHandler handler = (ConcernSelectSceneHandler) ((DisplayConcernSelectScene) RamApp
                            .getApplication().getCurrentScene()).getHandler();
                    handler.switchToPreviousScene((DisplayConcernSelectScene) RamApp.getApplication()
                            .getCurrentScene());
                }
                if (RamApp.getApplication().getCurrentScene() instanceof DisplayImpactModelSelectScene) {
                    DisplayImpactModelSelectScene scene = (DisplayImpactModelSelectScene) RamApp.getApplication()
                            .getCurrentScene();
                    scene.switchToPreviousScene();
                }
                if (sections.size() > 0) {
                    if (RamApp.getActiveScene() instanceof DisplayConcernEditScene) {
                        ConcernEditSceneHandler a = (ConcernEditSceneHandler) RamApp.getActiveScene().getHandler();
                        a.switchToHome((DisplayConcernEditScene) RamApp.getActiveScene());
                    } else {
                        if (RamApp.getActiveScene() instanceof DisplayClassDiagramScene) {
                            DisplayClassDiagramScene scene = (DisplayClassDiagramScene) RamApp.getActiveScene();

                            popSection();
                            scene.getHandler().switchToConcern(scene);
                            return true;
                            
                        } else if (RamApp.getActiveScene() instanceof DisplayAspectScene) {
                            selected = null;
                             
                            DisplayAspectScene scene = (DisplayAspectScene) RamApp.getActiveScene();
                            DisplayAspectSceneHandler handler =
                                    (DisplayAspectSceneHandler) RamApp.getActiveScene().getHandler();
                            if (scene.getCurrentView() instanceof StructuralDiagramView) {
                                if (scene.getPreviousScene() instanceof DisplayImpactModelEditScene) {
                                    histories.pop();
                                    popSection();
                                    DisplayImpactModelEditScene previousScene = 
                                            (DisplayImpactModelEditScene) scene.getPreviousScene();
                                    RamApp.getApplication().changeScene(scene.getPreviousScene());
                                    previousScene.repushGoal();
                                    previousScene.getCanvas().addChild(nav);
                                    RamApp.getApplication().closeAspectScene(scene);
                                } else {
                                    if (scene.getPreviousScene() instanceof DisplayAspectScene) {
                                        DisplayAspectScene previousScene = 
                                                (DisplayAspectScene) scene.getPreviousScene();
                                        Aspect previousAspect = previousScene.getAspect();
                                        if (topContainer.containsChild(returnToConcernButton)) {
                                            topContainer.removeAllChildren();
    
                                            handleSections();
    
                                            pushSection(currentConcern.getName(), getCoreNamer(), currentConcern);
                                            handler.switchBackTo((DisplayAspectScene) RamApp.getActiveScene());
    
                                            if (previousAspect.getName().contains(WOVEN_DELIMITER)) {
                                                weavingInterfaceMode(previousAspect);
                                                previousScene.repushSections();
                                            }
                                        } else {
                                            if (previousAspect.getName().contains(WOVEN_DELIMITER)) {
                                                weavingInterfaceMode(previousAspect);
                                                previousScene.repushSections();
                                            }
                                            handler.fastSwitchToConcern((DisplayAspectScene) RamApp.getActiveScene());
                                            history.remove(scene);
                                        }
                                    } else {
                                        if (scene.getPreviousScene() instanceof DisplayConcernSelectScene) {
                                            scene.setTransition(
                                                    new SlideTransition(RamApp.getApplication(), 700, false));
                                            handler.switchToConcern(scene);
                                        } else {
                                            handler.fastSwitchToConcern((DisplayAspectScene) RamApp.getActiveScene());
                                            history.remove(scene);
                                            histories.clear();
                                        }
                                    }
                                }
                            } else if (scene.getCurrentView() instanceof MessageViewView) {
                                if (topContainer.containsChild(returnToConcernButton)) {
                                    scene.switchToPreviousView();
                                    scene.repushSections();
                                } else {
                                    popSection();
                                    scene.switchToPreviousView();
                                    scene.repushSections();
                                }
                            } else if (scene.getCurrentView() instanceof StateDiagramView) {
                                popSection();
                                scene.switchToPreviousView();
                                scene.repushSections();
                            } else if (scene.getCurrentView() instanceof CompositionSplitEditingView) {
                                handler.closeSplitView(scene);
                            }
                        } else if (RamApp.getActiveScene() instanceof DisplayImpactModelEditScene) {
                            if (RamApp.getActiveScene().getPreviousScene() instanceof DisplayConcernEditScene
                                    || RamApp.getActiveScene().getPreviousScene() instanceof SelectAspectScene
                                    || RamApp.getActiveScene()
                                            .getPreviousScene() instanceof DisplayImpactModelEditScene) {
                                AbstractImpactScene aI = (AbstractImpactScene) RamApp.getActiveScene();
                                popSection();
                                aI.switchToPreviousScene();
                                if (RamApp.getActiveScene().getPreviousScene() instanceof DisplayImpactModelEditScene) {
                                    ((DisplayImpactModelEditScene) RamApp.getActiveScene().getPreviousScene())
                                            .repushGoal();
                                }
                            } else {
                                // I am going back FROM a impact to an aspect
                                DisplayAspectScene previousScene =
                                        (DisplayAspectScene) RamApp.getActiveScene().getPreviousScene();
                                AbstractImpactScene currentScene = (AbstractImpactScene) RamApp.getActiveScene();
                                currentScene.switchToPreviousScene();
                                previousScene.getCanvas().addChild(nav);
                                if (previousScene.getCurrentView() instanceof StructuralDiagramView) {
                                    previousScene.repushSections();
                                } else {
                                    previousScene.switchToPreviousView();
                                    previousScene.repushSections();
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    /**
     * Name delimiter for woven models.
     */
    private static final String WOVEN_DELIMITER = "_";

    private static NavigationBar nav = new NavigationBar();

    private static final float BACK_BUTTON_SIZE = 55;
    private static final String MENU_ARROW_KEY = "arrow";
    private static Stack<RamAbstractScene<?>> histories;

    private Stack<NavigationBarSection> sections;
    private RamRectangleComponent topContainer;
    private RamRectangleComponent menuContainer;
    private RamRectangleComponent rootContainer;
    private RamRoundedRectangleComponent backButton;
    private RamImageComponent image;
    private RamRectangleComponent acrossNotationContainerF;
    private RamRectangleComponent acrossNotationContainerRM;
    private RamRectangleComponent withinNotationContainer;
    private NavigationBarSection uniqueMenuSection;
    private NavigationBarMenu uniqueMenu;
    private NavigationBarMenu uniqueHistoryMenu;
    private Map<NavigationBarMenu, NavigationBarMenu> subMenus;
    private Deque<RamRectangleComponent> listOfWithin = new LinkedList<>();
    private Classifier selected;
    private NavigatonBarNamer<COREConcern> namerConcern;
    private NavigatonBarNamer<COREFeature> namerFeature;
    private NavigatonBarNamer<COREFeature> namerFeatConf;
    private NavigatonBarNamerRM<Aspect> namerRealizationModel;
    private LinkedHashMap<RamAbstractScene<?>, Aspect> history = new LinkedHashMap<RamAbstractScene<?>, Aspect>();
    private boolean historyOpen;
    private boolean containsWeaveCrumb;
    private RamButton returnToConcernButton;
    private COREConcern currentConcern;
    private COREPerspective currentPerspective;

    /**
     * Constructs a navigation bar and initialize it with no sections.
     */
    private NavigationBar() {
        super(new VerticalLayout());

        setPickable(false);
        sections = new Stack<NavigationBarSection>();
        histories = new Stack<RamAbstractScene<?>>();
        this.subMenus = new HashMap<NavigationBarMenu, NavigationBarMenu>();
        rootContainer = new RamRectangleComponent(new HorizontalLayoutVerticallyCentered(5f));
        // Ensure that the bar always appears the same way (horizontally centered) independent of the contents.
        rootContainer.setMinimumHeight(74f);
        rootContainer.setPickable(false);
        topContainer = new RamRectangleComponent(new HorizontalLayoutVerticallyCentered(5f));
        topContainer.setPickable(false);
        menuContainer = new RamRectangleComponent(new DefaultLayout());
        menuContainer.setPickable(false);
        RamScrollComponent scrollContainer = new RamScrollComponent(menuContainer);
        scrollContainer.setPickable(false);

        scrollContainer.setLayout(new DefaultLayout(0, RamApp.getApplication().getHeight() - BACK_BUTTON_SIZE));

        image = new RamImageComponent(Icons.ICON_MENU_BACK, MTColor.WHITE);

        backButton = new RamRoundedRectangleComponent(BACK_BUTTON_SIZE * 2, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE / 2);
        backButton.addChild(image);
        backButton.setAutoMaximizes(false);
        backButton.setAutoMinimizes(false);
        backButton.setNoStroke(true);
        backButton.setNoFill(false);
        backButton.setFillColor(Colors.MENU_BACKGROUND_COLOR);
        backButton.registerInputProcessor(new TapProcessor(RamApp.getApplication()));
        backButton.registerInputProcessor(new TapAndHoldProcessor(RamApp.getApplication(),
                GUIConstants.TAP_AND_HOLD_DURATION));
        BackMenuHandler menuHandler = new BackMenuHandler();
        backButton.addGestureListener(TapProcessor.class, menuHandler);
        backButton.addGestureListener(TapAndHoldProcessor.class, menuHandler);
        
        image.setPickable(false);
        image.setSizeLocal(BACK_BUTTON_SIZE - 20, BACK_BUTTON_SIZE - 20);
        float posX = (float) (0.75 * (backButton.getWidth() - image.getWidth()));
        float posY = (float) (0.5 * (backButton.getHeight() - image.getHeight()));
        image.setPositionRelativeToParent(new Vector3D(posX, posY));

        Vector3D navigationBarPosition = getPosition(TransformSpace.GLOBAL);
        navigationBarPosition.x -= BACK_BUTTON_SIZE;
        navigationBarPosition.y -= 5;
        setPositionGlobal(navigationBarPosition);

        // topContainer.addChild(backButton);
        rootContainer.addChild(backButton);
        rootContainer.addChild(topContainer);
        addChild(rootContainer);
        addChild(scrollContainer);
    }

    /**
     * Used to retrieve the navigation bar singleton.
     * 
     * @return the actual Navigation Bar
     */
    public static NavigationBar getInstance() {
        return nav;
    }

    /**
     * Updates the rotating back button accordingly.
     * 
     * @param expand boolean triggering the button expansion
     */
    public void updateHistoryButton(boolean expand) {

        PImage expandIcon = expand ? Icons.ICON_HISTORY_OPENED : Icons.ICON_MENU_BACK;
        image.setTexture(expandIcon);
    }

    /**
     * To push a section removing similar sections from the navigation bar.
     * 
     * @param icon icon of the section (not used)
     * @param label name of the section
     * @param namer used to create the actual section
     * @param eObject content of the section
     * @param <T> type of the {@link NavigationBarSection} related eObject
     */
    public <T> void pushSectionJump(PImage icon, String label, NavigatonBarNamer<T> namer, T eObject) {
        if (!sections.lastElement().retrieveTypeOfSection().equals(COREFeatureImpl.class)) {
            while (!sections.lastElement().retrieveTypeOfSection().equals(AspectImpl.class)) {
                popSection();
            }
        }

        pushSection(icon, label, namer, eObject);
    }

    /**
     * Used to push a Goal section inside the navigation bar.
     * 
     * @param icon for the icon section
     * @param label describing the section
     * @param eObject section object
     * @param <T> the current object type
     */
    public <T> void pushSectionGoal(PImage icon, String label, T eObject) {

        while (!sections.lastElement().retrieveTypeOfSection().equals(COREConcernImpl.class)) {
            popSection();
        }
        pushSection(icon, label, eObject);

    }

    /**
     * To push a Feature section in the bar.
     * 
     * @param icon icon of the section (not used)
     * @param label name of the section
     * @param namer used to create the actual section
     * @param eObject content of the section
     * @param <T> type of the {@link NavigationBarSection} related eObject
     */
    public <T> void pushSectionJumpFeature(PImage icon, String label, NavigatonBarNamer<T> namer, T eObject) {
        while (!sections.lastElement().retrieveTypeOfSection().equals(COREConcernImpl.class)) {
            popSection();
        }
        pushSection(icon, label, namer, eObject);

    }

    /**
     * To push a Realization Model - Aspect in the bar.
     * 
     * @param icon icon of the section (not used)
     * @param label name of the section
     * @param namer used to create the actual section
     * @param eObject content of the section
     * @param <T> type of the {@link NavigationBarSection} related eObject
     */
    public <T> void pushSectionJumpRM(PImage icon, String label, NavigatonBarNamerRM<T> namer, T eObject) {

        while (!sections.lastElement().retrieveTypeOfSection().equals(COREFeatureImpl.class)) {
            popSection();
        }

        pushSectionRM(icon, label, namer, eObject);
    }

    /**
     * To push a Realization Model - Aspect in the bar.
     * Called by pushSectionJumpRM after popping out the sections not needed.
     * 
     * @param icon icon of the section (not used)
     * @param label name of the section
     * @param namer used to create the actual section from a RM namer
     * @param eObject content of the section
     * @param <T> type of the {@link NavigationBarSection} related eObject
     */
    public <T> void pushSectionRM(PImage icon, String label, NavigatonBarNamerRM<T> namer, T eObject) {
        NavigationBarSection section = new NavigationBarSection(icon, label, namer, eObject);

        if (namer == null) {
            section.hideArrows();
        }

        if (eObject.getClass().equals(AspectImpl.class)) {
            if (this.sections.lastElement().retrieveTypeOfSection().equals(AspectImpl.class)) {
                // this means we are in a situation where Concern : RM . RM so the within model notation has to be added

                withinNotationContainer = new RamRectangleComponent();
                withinNotationContainer.setLayout(new VerticalLayout(-10));

                RamImageComponent withinModelNotation =
                        new RamImageComponent(Icons.ICON_FULLSTOP_WITHIN_MODEL, MTColor.WHITE);
                withinModelNotation.setSizeLocal(15, 45);
                withinModelNotation.setAutoMaximizes(false);
                withinModelNotation.setAutoMinimizes(false);
                withinModelNotation.setFillColor(MTColor.BLUE);
                withinNotationContainer.addChild(withinModelNotation);
                listOfWithin.add(withinNotationContainer);
                topContainer.addChild(withinNotationContainer);
            }
        }

        sections.push(section);
        topContainer.addChild(section);

        closeMenu();

        if (eObject.getClass().equals(COREFeatureImpl.class)) {
            acrossNotationContainerF = new RamRectangleComponent();
            acrossNotationContainerF.setLayout(new VerticalLayout());

            RamImageComponent acrossModelNotation = new RamImageComponent(Icons.ICON_COLON_ACROSS_MODEL, MTColor.WHITE);
            acrossModelNotation.setSizeLocal(8, 24);
            acrossModelNotation.setAutoMaximizes(false);
            acrossModelNotation.setAutoMinimizes(false);
            acrossModelNotation.setFillColor(MTColor.RED);
            acrossModelNotation.setPickable(false);
            acrossNotationContainerF.addChild(acrossModelNotation);
            topContainer.addChild(acrossNotationContainerF);
        }
    }

    /**
     * Add a new {@link NavigationBarSection} with an icon and a menu, in the section stack.
     * It closes opened menu.
     * 
     * @param icon - the icon in the {@link NavigationBarSection}
     * @param label - label of the {@link NavigationBarSection}
     * @param namer - namer of the {@link NavigationBarSection} for the menu creation.
     * @param eObject - eObject related to the {@link NavigationBarSection}
     * @param <T> - type of the {@link NavigationBarSection} related eObject.
     */
    public <T> void pushSection(PImage icon, String label, NavigatonBarNamer<T> namer, T eObject) {
        NavigationBarSection section = new NavigationBarSection(icon, label, namer, eObject);
        
        if (namer == null) {
            section.hideArrows();
        }
        // -------- we are pushing a StructuralView section in the stack ----------------
        if (eObject.getClass().equals(StructuralViewImpl.class)) {
            if (this.sections.lastElement().retrieveTypeOfSection().equals(AspectImpl.class)) {
                // this means it is the FIRST StructuralView element after the realization model, so= RM : Structural |
                // ACROSS
                acrossNotationContainerRM = new RamRectangleComponent();
                acrossNotationContainerRM.setLayout(new VerticalLayout());
                RamImageComponent acrossModelNotation =
                        new RamImageComponent(Icons.ICON_COLON_ACROSS_MODEL, MTColor.WHITE);
                acrossModelNotation.setSizeLocal(8, 24);
                acrossModelNotation.setAutoMaximizes(false);
                acrossModelNotation.setAutoMinimizes(false);
                acrossModelNotation.setFillColor(MTColor.RED);
                acrossModelNotation.setPickable(false);
                acrossNotationContainerRM.addChild(acrossModelNotation);
                topContainer.addChild(acrossNotationContainerRM);
            } else {
                // this means it is NOT the first StructuralView element, so = Structural . Structural | WITHIN
                withinNotationContainer = new RamRectangleComponent();
                withinNotationContainer.setLayout(new VerticalLayout());
                RamImageComponent withinModelNotation =
                        new RamImageComponent(Icons.ICON_FULLSTOP_WITHIN_MODEL, MTColor.WHITE);
                withinModelNotation.setSizeLocal(15, 45);
                withinModelNotation.setAutoMaximizes(false);
                withinModelNotation.setAutoMinimizes(false);
                withinModelNotation.setFillColor(MTColor.BLUE);
                withinNotationContainer.addChild(withinModelNotation);
                listOfWithin.add(withinNotationContainer);
                topContainer.addChild(withinNotationContainer);
            }
        } // -------- we are pushing a MessageView section in the stack ----------------
        if (eObject.getClass().equals(MessageViewImpl.class)
                || eObject.getClass().equals(AspectMessageViewImpl.class)
                || eObject.getClass().equals(MessageViewReferenceImpl.class)
                || eObject.getClass().equals(OperationImpl.class)) {
            if (this.sections.lastElement().retrieveTypeOfSection().equals(AspectImpl.class)) {
                // this means it is the FIRST MessageView element after the realization model, so= RM : Structural |
                // ACROSS
                acrossNotationContainerRM = new RamRectangleComponent();
                acrossNotationContainerRM.setLayout(new VerticalLayout());
                RamImageComponent acrossModelNotation =
                        new RamImageComponent(Icons.ICON_COLON_ACROSS_MODEL, MTColor.WHITE);
                acrossModelNotation.setSizeLocal(8, 24);
                acrossModelNotation.setAutoMaximizes(false);
                acrossModelNotation.setAutoMinimizes(false);
                acrossModelNotation.setFillColor(MTColor.RED);
                acrossNotationContainerRM.addChild(acrossModelNotation);
                topContainer.addChild(acrossNotationContainerRM);
            } else {
                // this means it is NOT the first MessageView element, so = Message . Message | WITHIN
                withinNotationContainer = new RamRectangleComponent();
                withinNotationContainer.setLayout(new VerticalLayout());
                RamImageComponent withinModelNotation =
                        new RamImageComponent(Icons.ICON_FULLSTOP_WITHIN_MODEL, MTColor.WHITE);
                withinModelNotation.setSizeLocal(15, 45);
                withinModelNotation.setAutoMaximizes(false);
                withinModelNotation.setAutoMinimizes(false);
                withinModelNotation.setFillColor(MTColor.BLUE);
                withinNotationContainer.addChild(withinModelNotation);
                listOfWithin.add(withinNotationContainer);
                topContainer.addChild(withinNotationContainer);
            }
        }
        // ------------- we are pushing a StateView section in the bar stack
        if (eObject.getClass().equals(StateViewImpl.class)) {
            if (this.sections.lastElement().retrieveTypeOfSection().equals(AspectImpl.class)) {
                // this means it is the FIRST StateView element after the realization model, so= RM : State | ACROSS
                acrossNotationContainerRM = new RamRectangleComponent();
                acrossNotationContainerRM.setLayout(new VerticalLayout());
                RamImageComponent acrossModelNotation =
                        new RamImageComponent(Icons.ICON_COLON_ACROSS_MODEL, MTColor.WHITE);
                acrossModelNotation.setSizeLocal(8, 24);
                acrossModelNotation.setAutoMaximizes(false);
                acrossModelNotation.setAutoMinimizes(false);
                acrossModelNotation.setFillColor(MTColor.RED);
                acrossNotationContainerRM.addChild(acrossModelNotation);
                topContainer.addChild(acrossNotationContainerRM);
            } else {
                // this means it is NOT the first MessageView element, so = State . State | WITHIN
                withinNotationContainer = new RamRectangleComponent();
                withinNotationContainer.setLayout(new VerticalLayout());
                RamImageComponent withinModelNotation =
                        new RamImageComponent(Icons.ICON_FULLSTOP_WITHIN_MODEL, MTColor.WHITE);
                withinModelNotation.setSizeLocal(15, 45);
                withinModelNotation.setAutoMaximizes(false);
                withinModelNotation.setAutoMinimizes(false);
                withinModelNotation.setFillColor(MTColor.BLUE);
                withinNotationContainer.addChild(withinModelNotation);
                listOfWithin.add(withinNotationContainer);
                topContainer.addChild(withinNotationContainer);
            }
        }
        if (eObject.getClass().equals(AspectImpl.class)) {

            if (this.sections.lastElement().retrieveTypeOfSection().equals(AspectImpl.class)) {
                // this means we are in a situation where Concern : RM . RM so the within model notation has to be added
                withinNotationContainer = new RamRectangleComponent();
                withinNotationContainer.setLayout(new VerticalLayout());
                RamImageComponent withinModelNotation =
                        new RamImageComponent(Icons.ICON_FULLSTOP_WITHIN_MODEL, MTColor.WHITE);
                withinModelNotation.setSizeLocal(15, 45);
                withinModelNotation.setAutoMaximizes(false);
                withinModelNotation.setAutoMinimizes(false);
                withinModelNotation.setFillColor(MTColor.BLUE);
                withinNotationContainer.addChild(withinModelNotation);
                listOfWithin.add(withinNotationContainer);
                topContainer.addChild(withinNotationContainer);
            }
        }
        if (eObject.getClass().equals(COREImpactNodeImpl.class)) {
            acrossNotationContainerRM = new RamRectangleComponent();
            acrossNotationContainerRM.setLayout(new VerticalLayout());

            RamImageComponent acrossModelNotation =
                    new RamImageComponent(Icons.ICON_COLON_ACROSS_MODEL, MTColor.WHITE);
            acrossModelNotation.setSizeLocal(8, 24);
            acrossModelNotation.setAutoMaximizes(false);
            acrossModelNotation.setAutoMinimizes(false);
            acrossModelNotation.setFillColor(MTColor.RED);
            acrossNotationContainerRM.addChild(acrossModelNotation);
            topContainer.addChild(acrossNotationContainerRM);
        }
        sections.push(section);
        // section.setNoStroke(false);
        topContainer.addChild(section);
        closeMenu();

        if (eObject.getClass().equals(COREFeatureImpl.class)) {
            acrossNotationContainerF = new RamRectangleComponent();
            acrossNotationContainerF.setLayout(new VerticalLayout());
            RamImageComponent acrossModelNotation = new RamImageComponent(Icons.ICON_COLON_ACROSS_MODEL, MTColor.WHITE);
            acrossModelNotation.setSizeLocal(8, 24);
            acrossModelNotation.setAutoMaximizes(false);
            acrossModelNotation.setAutoMinimizes(false);
            acrossModelNotation.setFillColor(MTColor.RED);
            acrossNotationContainerF.addChild(acrossModelNotation);
            topContainer.addChild(acrossNotationContainerF);

        }

    }

    /**
     * Add a new {@link NavigationBarSection} without icon but with a menu, in the section stack.
     * It closes opened menu.
     * 
     * @param label - label of the {@link NavigationBarSection}
     * @param namer - namer of the {@link NavigationBarSection} for the menu creation.
     * @param eObject - eObject related to the {@link NavigationBarSection}
     * @param <T> - type of the {@link NavigationBarSection} related eObject.
     */
    public <T> void pushSection(String label, NavigatonBarNamer<T> namer, T eObject) {
        pushSection(null, label, namer, eObject);
        // VARIANT FOR NO ICON, meaning the Concern section, the FIRST in the navigation bar
    }

    /**
     * Add a new {@link NavigationBarSection} with an icon but without a menu, in the section stack.
     * It closes opened menu.
     * 
     * @param icon - the icon in the {@link NavigationBarSection}
     * @param label - label of the {@link NavigationBarSection}
     * @param eObject - eObject related to the {@link NavigationBarSection}
     * @param <T> - type of the {@link NavigationBarSection} related eObject.
     */
    public <T> void pushSection(PImage icon, String label, T eObject) {
        pushSection(icon, label, null, eObject);
        // VARIANT FOR NO ARROW AT THE END
    }

    /**
     * Add a new {@link NavigationBarSection} without icon or menu, in the section stack.
     * It closes opened menu.
     * 
     * @param label - label of the {@link NavigationBarSection}
     * @param eObject - eObject related to the {@link NavigationBarSection}
     * @param <T> - type of the {@link NavigationBarSection} related eObject.
     */
    public <T> void pushSection(String label, T eObject) {
        pushSection(null, label, null, eObject);
    }

    /**
     * Remove the lately added section of the stack and close shown menus.
     * BASICALLY WHEN YOU CLICK ON BACK BUTTON
     */
    public void popSection() {
        if (sections.size() > 1) {
            NavigationBarSection section = sections.pop();
            if (section.retrieveTypeOfSection().equals(COREFeatureImpl.class)) {
                acrossNotationContainerF.removeAllChildren();
                topContainer.removeChild(acrossNotationContainerF);

            } else if (section.retrieveTypeOfSection().equals(StructuralViewImpl.class)
                    || section.retrieveTypeOfSection().equals(MessageViewImpl.class)
                    || section.retrieveTypeOfSection().equals(StateViewImpl.class)
                    || section.retrieveTypeOfSection().equals(AspectMessageViewImpl.class)
                    || section.retrieveTypeOfSection().equals(OperationImpl.class)
                    || section.retrieveTypeOfSection().equals(MessageViewReferenceImpl.class)
                    || section.retrieveTypeOfSection().equals(COREImpactNodeImpl.class)) {
                if (listOfWithin.size() > 1) {
                    RamRectangleComponent t = (RamRectangleComponent) listOfWithin.pop();
                    t.removeAllChildren();
                } else {
                    if (!listOfWithin.isEmpty()) {
                        RamRectangleComponent t = (RamRectangleComponent) listOfWithin.pop();
                        t.removeAllChildren();
                        topContainer.removeChild(withinNotationContainer);
                    } else {
                        // means we're in RM : SD so the ":" must be removed
                        acrossNotationContainerRM.removeAllChildren();
                        topContainer.removeChild(acrossNotationContainerRM);
                    }
                }
            } else if (section.retrieveTypeOfSection().equals(AspectImpl.class)) {
                // second check enabled only when peculiar situation of Realization Model with NO Feature happens
                if (topContainer.containsChild(acrossNotationContainerF)) {
                    acrossNotationContainerF.removeAllChildren();
                    topContainer.removeChild(acrossNotationContainerF);
                }
            }
            if (sections.lastElement().retrieveTypeOfSection().equals(COREFeatureImpl.class)) {
                popSection();
            }

            topContainer.removeChild(section);
            sections.peek().removeUnusedExpandButton();

            closeMenu();
        }
    }

    /**
     * Pop to a section when the LABEL is tapped.
     * Implementing the "Jump" action, used by a user by tapping on a section to which he wants to go.
     * 
     * @param wantedSection the section to which we want to Jump
     */
    public void popSection(Object wantedSection) {
        while (sections.size() > 1) {
            if (sections.lastElement().retrieveTypeOfSection().equals(wantedSection)) {
                break;
            }
            while (!sections.lastElement().retrieveTypeOfSection().equals(wantedSection)) {
                NavigationBarSection section = sections.pop();

                if (section.retrieveTypeOfSection().equals(COREFeatureImpl.class)) {

                    acrossNotationContainerF.removeAllChildren();
                    topContainer.removeChild(acrossNotationContainerF);

                    if (!histories.isEmpty()) {
                        if (histories.peek() instanceof DisplayAspectScene) {
                            RamApp.getApplication().invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    RamAbstractScene<?> a = histories.pop();

                                    while (a instanceof DisplayAspectScene) {
                                        DisplayAspectScene dA = (DisplayAspectScene) a;
                                        selected = null;

                                        RamApp.getApplication().changeScene(dA.getPreviousScene());
                                        popSection();
                                        dA.destroy();

                                        if (!histories.isEmpty()) {
                                            if (histories.peek() instanceof DisplayAspectScene) {
                                                a = histories.pop();
                                            } else {
                                                break;
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }

                            });

                        }
                    }
                } else if (section.retrieveTypeOfSection().equals(StructuralViewImpl.class)
                        || section.retrieveTypeOfSection().equals(MessageViewImpl.class)
                        || section.retrieveTypeOfSection().equals(StateViewImpl.class)
                        || section.retrieveTypeOfSection().equals(AspectMessageViewImpl.class)
                        || section.retrieveTypeOfSection().equals(OperationImpl.class)
                        || section.retrieveTypeOfSection().equals(MessageViewReferenceImpl.class)) {
                    if (listOfWithin.size() > 1) {
                        RamRectangleComponent t = (RamRectangleComponent) listOfWithin.pop();
                        t.removeAllChildren();
                    } else {
                        if (!listOfWithin.isEmpty()) {
                            RamRectangleComponent t = (RamRectangleComponent) listOfWithin.pop();
                            t.removeAllChildren();
                            topContainer.removeChild(withinNotationContainer);
                        } else {
                            // means we're in RM : SD so the ":" must be removed
                            acrossNotationContainerRM.removeAllChildren();
                            topContainer.removeChild(acrossNotationContainerRM);
                        }
                    }

                    RamApp.getActiveAspectScene().switchToView(RamApp.getActiveAspectScene()
                            .getStructuralDiagramView());
                } else if (section.retrieveTypeOfSection().equals(AspectImpl.class)
                        && acrossNotationContainerRM != null) {
                    acrossNotationContainerRM.removeAllChildren();
                    topContainer.removeChild(acrossNotationContainerRM);
                } else if (section.retrieveTypeOfSection().equals(COREImpactNodeImpl.class)) {
                    acrossNotationContainerRM.removeAllChildren();
                    topContainer.removeChild(acrossNotationContainerRM);
                    AbstractImpactScene aI = (AbstractImpactScene) RamApp.getActiveScene();
                    aI.switchToPreviousScene();
                }

                topContainer.removeChild(section);

                closeMenu();
            }
        }
    }

    /**
     * Method to specifically return back to the Concern scene removing every other scene that could be present.
     * 
     * @param wantedSection COREConcern class
     * @param obj COREConcern EObject
     */
    public void popSection(Object wantedSection, EObject obj) {
        if (!sections.lastElement().retrieveTypeOfSection().equals(wantedSection)) {
            histories.clear();

            if (acrossNotationContainerF != null) {
                acrossNotationContainerF.removeAllChildren();
                topContainer.removeChild(acrossNotationContainerF);
            }
            if (acrossNotationContainerRM != null) {
                acrossNotationContainerRM.removeAllChildren();
                topContainer.removeChild(acrossNotationContainerRM);
            }
            while (sections.size() > 1) {
                topContainer.removeChild(sections.pop());
            }

            // CHECKSTYLE:IGNORE AnonInnerLength: Okay here.
            RamApp.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    // TODO: Here getCurrentScene could be used instead of for loop?
                    for (Iscene scene : RamApp.getApplication().getScenes()) {
                        if (scene instanceof DisplayAspectScene) {
                            DisplayAspectScene aspectScene = (DisplayAspectScene) scene;
                            if (aspectScene.getPreviousScene() instanceof DisplayConcernEditScene) {
                                DisplayAspectSceneHandler handler = 
                                        (DisplayAspectSceneHandler) aspectScene.getHandler();
                                ((RamAbstractScene<?>) RamApp.getApplication().getCurrentScene())
                                        .setTransition(new SlideTransition(RamApp.getApplication(), 700, false));
                                handler.fastSwitchToConcern((DisplayAspectScene) scene);
                            }
                        } else if (scene instanceof DisplayImpactModelEditScene) {
                            DisplayImpactModelEditScene impactScene = (DisplayImpactModelEditScene) scene;
                            if (impactScene.getPreviousScene() instanceof DisplayConcernEditScene) {
                                impactScene.switchToPreviousScene();
                            }
                        }
                    }
                    
                    // TODO: Can this be within the above for loop?
                    for (Iscene scene : RamApp.getApplication().getScenes()) {
                        if (scene instanceof DisplayAspectScene) {
                            if (RamApp.getApplication().getCurrentScene().equals(scene)) {
                                RamApp.getApplication().destroySceneAfterTransition(scene);
                            } else {
                                RamApp.getApplication().closeAspectScene((DisplayAspectScene) scene);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Opens the given menu for the given section at the given position. If there is already a menu, it will be
     * replaced (removed).
     * 
     * @param section - related section of the given menu.
     * @param menu - menu to show.
     * @param position - position of the menu.
     */
    public void openMenu(final NavigationBarSection section, final NavigationBarMenu menu, final Vector3D position) {
        closeMenu();
        if (historyOpen) {
            closeHistoryMenu();
            historyOpen = false;
        }
        
        RamApp.getApplication().invokeLater(new Runnable() {

            @Override
            public void run() {
                uniqueMenuSection = section;
                uniqueMenu = menu;
                uniqueMenuSection.updateExpandButton(true);
                menuContainer.addChild(menu, true);
                position.y += topContainer.getHeightXY(TransformSpace.GLOBAL) / 2.4;
                position.x = uniqueMenuSection.getPosition(TransformSpace.GLOBAL).x;
                uniqueMenu.setPositionGlobal(position);
                menuContainer.updateLayout();
            }
        });
    }

    /**
     * Shows up the History menu under the Back button of the navbar.
     * 
     * @param menu the history menu itself
     * @param position positions the menu under the back button
     */
    public void openHistoryMenu(final NavigationBarMenu menu, final Vector3D position) {
        closeMenu();

        RamApp.getApplication().invokeLater(new Runnable() {

            @Override
            public void run() {
                uniqueHistoryMenu = menu;
                menuContainer.addChild(menu, true);
                position.y += backButton.getHeightXY(TransformSpace.GLOBAL);
                uniqueHistoryMenu.setPositionGlobal(position);
                menuContainer.updateLayout();
                updateHistoryButton(true);
            }
        });
    }

    /**
     * Method for closing the History menu showing up under the Back button.
     */
    public void closeHistoryMenu() {
        RamApp.getApplication().invokeLater(new Runnable() {

            @Override
            public void run() {
                menuContainer.removeChild(uniqueHistoryMenu);
                updateHistoryButton(false);
            }

        });
    }

    /**
     * Close the currently shown menu and its sub-menus.
     */
    public void closeMenu() {
        RamApp.getApplication().invokeLater(new Runnable() {

            @Override
            public void run() {
                if (uniqueMenuSection != null && uniqueMenu != null) {
                    closeSubMenu(uniqueMenu);
                    uniqueMenuSection.updateExpandButton(false);
                    uniqueMenuSection.updateUpArrowSwitch(false);
                    menuContainer.removeChild(uniqueMenu);
                    uniqueMenuSection = null;
                    uniqueMenu = null;
                    if (selected != null) {

                        ClassifierView<?> w = RamApp.getActiveAspectScene()
                                .getStructuralDiagramView().getClassViewOf(selected);
                        w.setFillColor(Colors.DEFAULT_ELEMENT_FILL_COLOR);
                        selected = null;
                    }
                }
            }

        });

        closeMenuFanOut();
    }

    /**
     * Used to open the secondary menu for the Filtered Switch action.
     * 
     * @param section of the bar to which the filtered switch menu should appear
     * @param menu the actual menu we are in
     * @param position position for the Menu to appear
     */
    public void openMenuFanOut(final NavigationBarSection section,
            final NavigationBarMenu menu,
            final Vector3D position) {
        closeMenu();
        closeMenuFanOut();
        if (selected != null) {
            ClassifierView<?> w = RamApp.getActiveAspectScene()
                    .getStructuralDiagramView().getClassViewOf(selected);
            w.setFillColor(Colors.DEFAULT_ELEMENT_FILL_COLOR);
            selected = null;
        }

        RamApp.getApplication().invokeLater(new Runnable() {

            @Override
            public void run() {
                uniqueMenuSection = section;
                // uniqueMenuSection.setNoStroke(false);
                // uniqueMenuSection.setStrokeColor(MTColor.BLUE);
                uniqueMenu = menu;
                uniqueMenuSection.updateUpArrowSwitch(true);
                menuContainer.addChild(menu, true);
                position.y += topContainer.getHeightXY(TransformSpace.GLOBAL) / 2.4;
                position.x = uniqueMenuSection.getPosition(TransformSpace.GLOBAL).x;
                uniqueMenu.setPositionGlobal(position);
                menuContainer.updateLayout();
            }
        });
    }

    /**
     * Method for closing the Filtered Switch menu created previously.
     */
    public void closeMenuFanOut() {
        RamApp.getApplication().invokeLater(new Runnable() {

            @Override
            public void run() {
                if (uniqueMenuSection != null && uniqueMenu != null) {
                    closeSubMenu(uniqueMenu);
                    uniqueMenuSection.updateExpandButton(false);
                    uniqueMenuSection.updateUpArrowSwitch(false);
                    menuContainer.removeChild(uniqueMenu);
                    uniqueMenuSection = null;
                    uniqueMenu = null;
                }
            }
        });
    }

    /**
     * Opens the given sub-menu related to the given menu at the given position. If a sub-menu is already shown for the
     * given menu, it will be replaced (removed).
     * 
     * @param menu - related menu of the given sub-menu.
     * @param subMenu - the sub-menu to show.
     * @param position - global position of the sub-menu.
     */
    public void openSubMenu(NavigationBarMenu menu, NavigationBarMenu subMenu, Vector3D position) {
        closeSubMenu(menu);
        menuContainer.addChild(subMenu, true);
        position.x = menu.getPosition(TransformSpace.GLOBAL).x + menu.getWidthXY(TransformSpace.GLOBAL);
        subMenu.setPositionGlobal(position);
        // --- Arrow
        RamImageComponent arrow = new RamImageComponent(Icons.ICON_NAVIGATION_COLLAPSE, MTColor.WHITE);
        arrow.setFillColor(Colors.MENU_BACKGROUND_COLOR);
        arrow.setSizeLocal(35, 35);
        menuContainer.addChild(arrow);
        Vector3D arrowPosition = position;
        arrowPosition.y -= arrow.getHeightXY(TransformSpace.GLOBAL) / 2;
        arrowPosition.x -= 2 * arrow.getWidth() / 3 - 2;
        arrow.setPositionGlobal(position);
        // ----
        menu.setUserData(MENU_ARROW_KEY, arrow);
        menuContainer.updateLayout();
        subMenus.put(menu, subMenu);
    }

    /**
     * Closes the shown sub-menu of the given menu if there is one. In this case it will replace it (remove) and do the
     * same for its sub-menus.
     * 
     * @param menu - related menu of the sub-menu to close.
     */
    public void closeSubMenu(final NavigationBarMenu menu) {
        NavigationBarMenu subMenu = subMenus.get(menu);
        if (subMenu != null) {
            closeSubMenu(subMenu);
            menuContainer.removeChild((RamImageComponent) menu.getUserData(MENU_ARROW_KEY));
            menuContainer.removeChild(subMenu, true);
            subMenus.remove(menu);
        }

    }

    /**
     * Namer used for representing a COREConcern and a COREImpactNode inside the navigation bar.
     * 
     * @return the namer itself
     */
    public NavigatonBarNamer<COREConcern> getCoreNamer() {
        namerConcern = new NavigationBarMenu.NavigatonBarNamer<COREConcern>() {
            @Override
            public void initMenu(NavigationBarMenu menu, final COREConcern element) {
                currentConcern = element;
                menu.addMenuElement(Strings.LABEL_FEATURE, element.getFeatureModel(),
                        CorePackage.Literals.CORE_FEATURE_MODEL__FEATURES, null, getFeatureNamer(), null);
                if (element.getImpactModel() != null) {
                    createGoalsMenu(menu, element);
                }
                @SuppressWarnings("unchecked")
                LinkedList<Aspect> aspectsL = new LinkedList<Aspect>();
//                        (Collection<? extends Aspect>) EMFModelUtil.collectElementsOfType(element,
//                                CorePackage.Literals.CORE_CONCERN__ARTEFACTS, RamPackage.eINSTANCE.getAspect()));

                createDesignModels(menu, aspectsL);
            }

        };
        
        return namerConcern;
    }

    /**
     * Method called by the COREConcern Namer to represent Design Models submenu shortcut.
     * 
     * @param menu menu to add the Design Models
     * @param aspectsL List containing the found Design Models
     */
    private void createDesignModels(NavigationBarMenu menu, List<Aspect> aspectsL) {
        menu.addMenuElement(Strings.LABEL_MODELS_DESIGN, aspectsL, new AbstractDefaultListListener<Aspect>() {
            
            @Override
            public void elementSelected(RamListComponent<Aspect> list, Aspect element) {
                handleChosenAspect(element);
            }
            
        }, null, true);
    }

    /**
     * Method called by the COREConcern Namer to represent Goals submenu.
     * 
     * @param menu menu where to add the Goals
     * @param element the COREConcern where to look for Impact Goals
     */
    private static void createGoalsMenu(NavigationBarMenu menu, final COREConcern element) {

        menu.addMenuElement(Strings.LABEL_MODELS_GOALS, element.getImpactModel(),
                CorePackage.Literals.CORE_IMPACT_MODEL__IMPACT_MODEL_ELEMENTS, null,
                new AbstractDefaultListListener<COREImpactNode>() {
                    @Override
                    public void elementSelected(RamListComponent<COREImpactNode> list, COREImpactNode elementC) {
                        if (RamApp.getActiveScene() instanceof DisplayAspectScene) {
                            DisplayAspectScene a = (DisplayAspectScene) RamApp.getActiveScene();
                            histories.push(a);
                        }
                        RamApp.getActiveScene().setTransition(new SlideTransition(
                                RamApp.getApplication(), 700, true));
                        RamApp.getApplication().showImpactModel(element, elementC);
                    }

                }, new Filter<COREImpactNode>() {

                    @Override
                    public boolean shouldAddElement(COREImpactNode element) {
                        return !(element instanceof COREFeatureImpactNode);
                    }
                });
    }

    /**
     * Function to load a new aspect when residing inside an aspect already.
     * Called also by the confirm popup after checking if the current aspect needs saving.
     * 
     * @param currentAspect aspect SCENE to transition from
     * @param element Aspect element to go to
     */
    public void loadNewAspect(DisplayAspectScene currentAspect, Aspect element) {
        history.put(currentAspect, currentAspect.getAspect());
        histories.push(currentAspect);
        
        EObject model = EcoreUtil.getRootContainer(element);
        COREExternalArtefact artefact = COREArtefactUtil.getReferencingExternalArtefact(model);

        if (history.containsValue(element)) {
            currentAspect.setTransition(
                    new SlideTransition(RamApp.getApplication(), 700, true));
            RamApp.getApplication().loadScene(artefact, model);
            RamAbstractScene<?> scene = RamApp.getApplication().getExistingOrCreateModelScene(element);
            if (scene instanceof DisplayAspectScene) {
                ((DisplayAspectScene) scene).repushSections();
            } else if (scene instanceof DisplayClassDiagramScene) {
                ((DisplayClassDiagramScene) scene).repushSections();
            }
            RamApp.getApplication().getExistingOrCreateModelScene(element)
                    .getCanvas().addChild(nav);
            for (RamAbstractScene<?> sc : history.keySet()) {
                if (history.get(sc).equals(element)) {
                    RamApp.getApplication().removeScene(sc);
                    break;
                }
            }
        } else {
            currentAspect.setTransition(new SlideTransition(
                    RamApp.getApplication(), 700, true));
            RamApp.getApplication().loadScene(artefact, model);
        }
    }

    /**
     * Namer used to represent and push a COREFeature section inside the navigation bar.
     * 
     * @return the Feature itself
     */
    public NavigatonBarNamer<COREFeature> getFeatureNamer() {
        namerFeature = new NavigationBarMenu.NavigatonBarNamer<COREFeature>() {
            
            @Override
            public void initMenu(NavigationBarMenu menu, COREFeature feature) {
                realizeDesignModels(menu, feature);
                realizeConflictResolutions(menu, feature);
            }
        };
        
        return namerFeature;
    }

    /**
     * Namer used to retrieve secondary RMs and potential Conflict Resolution Models from the Feature section.
     * 
     * @return Namer containing possible menu populated with RMs and CRMs
     */
    public NavigatonBarNamer<COREFeature> getFeatureAndConflict() {
        namerFeatConf = new NavigationBarMenu.NavigatonBarNamer<COREFeature>() {
            
            @Override
            public void initMenu(NavigationBarMenu menu, COREFeature element) {
                // When clicking on the arrow below the feature the user will always be already in a RM
                final Aspect currentAspect = RamApp.getActiveAspectScene().getAspect();

                lookForSecondaryRMs(currentAspect, menu, element);
                realizeConflictResolutions(menu, element);
            }
        };

        return namerFeatConf;
    }

    /**
     * Looks for secondary Realization Models to span from the Feature section menu.
     * 
     * @param currentAspect the Design Model one is in that must not be displayed (used by Filter function)
     * @param menu menu where to add secondary features
     * @param element Feature to examine
     */
    private void lookForSecondaryRMs(final Aspect currentAspect, NavigationBarMenu menu, COREFeature element) {
        menu.addMenuElement(Strings.LABEL_MODELS_REALIZATION, element, CorePackage.Literals.CORE_FEATURE__REALIZED_BY,
                // CHECKSTYLE:IGNORE AnonInnerLength: Okay here.
                RamPackage.Literals.ASPECT, new AbstractDefaultListListener<Aspect>() {
                    @Override
                    public void elementSelected(RamListComponent<Aspect> list, Aspect element) {
                        DisplayAspectScene currentAspectScene = (DisplayAspectScene) RamApp.getActiveScene();
                        Aspect currentAspect = currentAspectScene.getAspect();
                        if (!currentAspect.equals(element)) {
                            history.put(currentAspectScene, currentAspect);
                            histories.push(currentAspectScene);
                            if (history.containsValue(element)) {
                                currentAspectScene.setTransition(
                                        new SlideTransition(RamApp.getApplication(), 700, true));
                                RamApp.getApplication().loadScene(
                                        COREArtefactUtil.getReferencingExternalArtefact(element), element);
                                RamAbstractScene<?> scene = RamApp.getApplication()
                                        .getExistingOrCreateModelScene(element);
                                if (scene instanceof DisplayAspectScene) {
                                    ((DisplayAspectScene) scene).repushSections();
                                } else if (scene instanceof DisplayClassDiagramScene) {
                                    ((DisplayClassDiagramScene) scene).repushSections();
                                }
                                RamApp.getApplication().getExistingOrCreateModelScene(element)
                                        .getCanvas().addChild(nav);
                            } else {
                                currentAspectScene.setTransition(new SlideTransition(
                                        RamApp.getApplication(), 700, true));
                                RamApp.getApplication().loadScene(
                                        COREArtefactUtil.getReferencingExternalArtefact(element), element);
                            }
                        }
                    }

                }, new Filter<Aspect>() {
                    @Override
                    public boolean shouldAddElement(Aspect element) {
                        if (element != currentAspect) {
                            COREArtefact externalArtefact = COREArtefactUtil.getReferencingExternalArtefact(element);
                            return externalArtefact.getScene().getRealizes().size() == 1;
                        } else {
                            return false;
                        }
                    }
                });
    }

    /**
     * Finds all the Realization Models present in the feature.
     * Differently from createDesignModels(NavigationBarMenu, List<Aspect>) this method
     * handles subdivision through different features, thus not creating "Design Models" shortcuts
     * 
     * @param menu menu to add models found.
     * @param feature feature to examine for potential RMs
     */
    public void realizeDesignModels(NavigationBarMenu menu, COREFeature feature) {
        menu.addMenuElement(Strings.LABEL_MODELS_REALIZATION, feature, CorePackage.Literals.CORE_FEATURE__REALIZED_BY,
                RamPackage.Literals.ASPECT, new AbstractDefaultListListener<Aspect>() {
                    @Override
                    public void elementSelected(RamListComponent<Aspect> list, Aspect element) {
                        handleChosenAspect(element);
                    }

                }, new Filter<Aspect>() {
                    @Override
                    public boolean shouldAddElement(Aspect element) {
                        COREArtefact externalArtefact = COREArtefactUtil.getReferencingExternalArtefact(element);
                        return externalArtefact.getScene().getRealizes().size() == 1;
                    }
                });
    }

    /**
     * Method called both createDesignModels and realizeDesignModels to operate on the chosen Aspect.
     * 
     * @param element the Aspect one wants to open -> DisplayAspectScene
     */
    private void handleChosenAspect(Aspect element) {
        if (RamApp.getActiveScene() instanceof DisplayConcernEditScene) {
            RamApp.getActiveScene().setTransition(
                    new SlideTransition(RamApp.getApplication(), 700, true));
            RamApp.getApplication().loadScene(
                    COREArtefactUtil.getReferencingExternalArtefact(element), element);
        } else if (RamApp.getActiveScene() instanceof DisplayAspectScene) {
            DisplayAspectScene currentAspect = (DisplayAspectScene) RamApp.getActiveScene();
            if (!currentAspect.getAspect().equals(element)) {
                boolean isSaveNeeded = EMFEditUtil
                        .getCommandStack(((DisplayAspectScene) RamApp.getApplication().getCurrentScene()).getAspect())
                        .isSaveNeeded();
                if (isSaveNeeded) {
                    DisplayAspectSceneHandler d =
                            (DisplayAspectSceneHandler) ((DisplayAspectScene) RamApp.getApplication().getCurrentScene())
                                    .getHandler();
                    d.showCloseConfirmNextAspect((DisplayAspectScene) RamApp.getApplication().getCurrentScene(),
                            element);
                } else {
                    loadNewAspect(currentAspect, element);
                }
            }
        } else if (RamApp.getActiveScene() instanceof DisplayImpactModelEditScene) {
            popSection();
            DisplayImpactModelEditScene aI = (DisplayImpactModelEditScene) RamApp.getActiveScene();
            // aI.switchToPreviousScene();
            if (RamApp.getActiveScene().getPreviousScene() instanceof DisplayAspectScene) {
                DisplayAspectScene currentAspect = (DisplayAspectScene) RamApp.getActiveScene().getPreviousScene();
                if (!currentAspect.getAspect().equals(element)) {
                    history.put(currentAspect, currentAspect.getAspect());
                    if (history.containsValue(element)) {
                        currentAspect.setTransition(
                                new SlideTransition(RamApp.getApplication(), 700, true));
                        RamApp.getApplication().loadScene(
                                COREArtefactUtil.getReferencingExternalArtefact(element), element);
                        RamAbstractScene<?> scene = RamApp.getApplication().getExistingOrCreateModelScene(element);
                        if (scene instanceof DisplayAspectScene) {
                            ((DisplayAspectScene) scene).repushSections();
                        } else if (scene instanceof DisplayClassDiagramScene) {
                            ((DisplayClassDiagramScene) scene).repushSections();
                        }
                        RamApp.getApplication().getExistingOrCreateModelScene(element)
                                .getCanvas().addChild(nav);
                    } else {
                        RamApp.getApplication().closeAspectScene(currentAspect);
                        RamApp.getApplication().loadScene(
                                COREArtefactUtil.getReferencingExternalArtefact(element), element);
                    }
                }
            } else {
                RamApp.getApplication().loadScene(
                        COREArtefactUtil.getReferencingExternalArtefact(element), element);
                histories.push(aI);
            }
        }
    }

    /**
     * Finds potential Conflict resolution model and adds them to the Feature submenu.
     * 
     * @param menu menu to add the found CRM
     * @param feature feature used to look for CRMs
     */
    public void realizeConflictResolutions(NavigationBarMenu menu, COREFeature feature) {
        menu.addMenuElement(Strings.LABEL_MODELS_CONFLICT, feature, CorePackage.Literals.CORE_FEATURE__REALIZED_BY,
                RamPackage.Literals.ASPECT, new AbstractDefaultListListener<COREArtefact>() {
                    @Override
                    public void elementSelected(RamListComponent<COREArtefact> list, COREArtefact element) {
                        COREFeature al = element.getScene().getRealizes().get(0);
                        Collection<Aspect> aspects = EMFModelUtil.collectElementsOfType(al,
                                CorePackage.Literals.CORE_FEATURE__REALIZED_BY,
                                RamPackage.Literals.ASPECT);
                        for (Aspect aspect : aspects) {
                            if (aspect.getName().equals(element.getName())) {
                                handleChosenAspect(aspect);
                            }
                        }
                    }

                }, new Filter<COREArtefact>() {
                    @Override
                    public boolean shouldAddElement(COREArtefact element) {
                        return element.getScene().getRealizes().size() > 1;
                    }
                });
    }

    /**
     * Namer to handle and show a Realization Model section in the bar.
     * 
     * @return the RM namer itself
     */
    public NavigatonBarNamerRM<Aspect> getRMNamer() {
        namerRealizationModel = new NavigationBarMenu.NavigatonBarNamerRM<Aspect>() {
            // Menu for RIGHT arrow of Realization Model
            @Override
            public void initMenu(NavigationBarMenu menu, final Aspect element) {

                realizeStructuralView(menu, element);
                realizeMessageView(menu, element);
                realizeStateView(menu, element);
                if (!element.getName().contains(WOVEN_DELIMITER)) {
                    realizeConcernReuses(menu, element);
                }
            }

            // Menu for LEFT arrow of Realization Model
            @Override
            public void initFanOutMenu(NavigationBarMenu menu, final Aspect element) {
                realizeFeaturesAndConflictResolutionM(menu, element);
                realizeExtendedDesignModels(menu, element);
            }
        };
        return namerRealizationModel;
    }

    /**
     * Method called from the RM Namer to realize submenu for Structural View classes.
     * 
     * @param menu menu where elements will be added
     * @param element element containing the classes
     */
    public void realizeStructuralView(NavigationBarMenu menu, Aspect element) {
        menu.addMenuElement(Strings.LABEL_STRUCTURE_VIEW, element.getStructuralView()
                .getClasses(), getNamerClassifier(), null);
    }

    /**
     * Method called from Design Model to realize submenus relative to MessageViews and also AMVs.
     * 
     * @param menu menu to add elements to
     * @param element Aspect element from which information are taken
     */
    public static void realizeMessageView(NavigationBarMenu menu, Aspect element) {
        List<MessageView> messageViews = new ArrayList<>();
        List<AspectMessageView> aspectMessageViews =
                new ArrayList<>(MessageViewUtil.getMessageViewsOfType(element, AspectMessageView.class));

        for (MessageView messageView : MessageViewUtil.getMessageViewsOfType(element, MessageView.class)) {
            Operation specifies = messageView.getSpecifies();
            boolean isPartial = specifies.getPartiality() != RAMPartialityType.NONE;
            boolean isAbstract = specifies.isAbstract();

            if (!isPartial && !isAbstract) {
                messageViews.add(messageView);
            }
        }
        
        menu.addMenuElement(Strings.LABEL_MESSAGE_VIEW, messageViews, new AbstractDefaultListListener<MessageView>() {
            @Override
            public void elementSelected(RamListComponent<MessageView> list, MessageView messageView) {
                RamApp.getActiveAspectScene().showMessageView(messageView);
            }

        }, null);

        menu.addMenuElement(Strings.LABEL_ASPECT_MESSAGE_VIEW, aspectMessageViews,
            new AbstractDefaultListListener<AspectMessageView>() {
                @Override
                public void elementSelected(RamListComponent<AspectMessageView> list, AspectMessageView element) {
                    RamApp.getActiveAspectScene().showMessageView(element);
                }
    
            }, null, true);
    }

    /**
     * Method to realize StateView view for specific Realization Model.
     * 
     * @param menu specific menu to add the state view to
     * @param element Aspect element to examine in search for state views
     */
    public void realizeStateView(NavigationBarMenu menu, Aspect element) {
        menu.addMenuElement(Strings.LABEL_STATE_VIEW, element.getStateViews(),
                new AbstractDefaultListListener<StateView>() {
                    @Override
                    public void elementSelected(RamListComponent<StateView> list, StateView element) {
                        closeMenu();
                        RamApp.getActiveAspectScene()
                                .switchToView(RamApp.getActiveAspectScene().getStateView());
                    }

                }, null);
    }

    /**
     * Method handling potential Concern Reuses of a Design Model one's in at that moment.
     * 
     * @param menu Menu to add the potentially found reuses
     * @param element Aspect element used to find whether is reusing other Models.
     */
    public void realizeConcernReuses(NavigationBarMenu menu, final Aspect element) {
        List<COREReuse> reuses = new ArrayList<>();
        final HashMap<COREReuse, COREModelReuse> modelReuseMap = new HashMap<COREReuse, COREModelReuse>();
        COREArtefact externalArtefact = COREArtefactUtil.getReferencingExternalArtefact(element);
        
        for (COREModelReuse modelReuse : externalArtefact.getModelReuses()) {
            if (!Constants.ASSOCIATION_CONCERN_NAME
                    .equals(modelReuse.getReuse().getReusedConcern().getName())) {
                reuses.add(modelReuse.getReuse());
                modelReuseMap.put(modelReuse.getReuse(), modelReuse);
            }
        }
        menu.addMenuElement(Strings.LABEL_MODEL_REUSE, reuses, new AbstractDefaultListListener<COREReuse>() {
            @Override
            public void elementSelected(RamListComponent<COREReuse> list, COREReuse reuse) {
                COREModelComposition modelComposition = modelReuseMap.get(reuse);
                DisplayAspectScene aspectScene = (DisplayAspectScene) RamApp.getActiveScene();
                
                if (!(aspectScene.getCurrentView() instanceof StructuralDiagramView)) {
                    popSection();
                    aspectScene.switchToPreviousView();
                }
                aspectScene.setTransition(new BlendTransition(RamApp.getApplication(), 700));

                collapseStartingEnvironment(element);
                Aspect externalAspect = (Aspect) modelComposition.getSource();
                RamApp.getApplication().changeScene(RamApp.getApplication()
                        .getExistingOrCreateModelScene(externalAspect));
            }

        }, null);
    }

    /**
     * Looks for other Features implementing the same RM and also for Conflict Resolution Models of that RM.
     * 
     * @param menu Menu to add the information that is potentially found
     * @param element Aspect element used to retrieve information
     */
    public static void realizeFeaturesAndConflictResolutionM(NavigationBarMenu menu, final Aspect element) {
        LinkedList<COREFeature> realizedFeatures = new LinkedList<COREFeature>();
        COREExternalArtefact artefact = (COREExternalArtefact) COREArtefactUtil.getReferencingExternalArtefact(element);
        if (artefact.getScene() != null) {
            for (int i = 1; i < artefact.getScene().getRealizes().size(); i++) {
                realizedFeatures.add(artefact.getScene().getRealizes().get(i));
            }
        } else {
            // woven models do not realize any features, so in this case there is nothing to do
            return;
        }
        
        // SHOW all the features that have THIS RM inside and switch among them when clicked
        if (realizedFeatures.size() > 0) {
            menu.addMenuElement(Strings.LABEL_FEATURES,
                    realizedFeatures,
                    new AbstractDefaultListListener<COREFeature>() {

                        @Override
                        public void elementSelected(RamListComponent<COREFeature> list, COREFeature element) {

                        }

                    }, null);
        }
        
        if (artefact.getScene().getRealizes().size() > 0) {
            //TODO: we need to figure out how we want to display conflict resolution realization models 
//            menu.addMenuElement(Strings.LABEL_MODELS_CONFLICT, artefact.getScene(),
//                    CorePackage.Literals.CORE_SCENE__ARTEFACTS,
//                    RamPackage.Literals.ASPECT, new AbstractDefaultListListener<COREArtefact>() {
//                        @Override
//                        public void elementSelected(RamListComponent<COREArtefact> list, COREArtefact element) {
//                            RamApp.getApplication().loadScene((COREExternalArtefact) element,
//                                (Aspect) ((COREExternalArtefact) element).getRootModelElement());
//                        }
//
//                    }, new Filter<COREArtefact>() {
//                        @Override
//                        public boolean shouldAddElement(COREArtefact element) {
//                            return artefact.getScene().getRealizes().size() > 1;
//                        }
//                    });
        }
    }

    /**
     * Looks for specific Design Models that the Aspect we're in extends.
     * 
     * @param menu Menu to add extending Models in
     * @param aspect Aspect interrogated to see if it extends any other Model.
     */
    public void realizeExtendedDesignModels(NavigationBarMenu menu, final Aspect aspect) {
        LinkedList<COREArtefact> extendedAspects = new LinkedList<COREArtefact>();
        extendedAspects.addAll(COREModelUtil.collectExtendedModels(aspect));

        menu.addMenuElement(Strings.LABEL_MODEL_EXTENDS, extendedAspects,
                new AbstractDefaultListListener<COREArtefact>() {
                    @Override
                    public void elementSelected(RamListComponent<COREArtefact> list, COREArtefact artefact) {
                        COREExternalArtefact cea = (COREExternalArtefact) artefact;
                        Aspect element = (Aspect) cea.getRootModelElement();
                        DisplayAspectScene currentAspect = (DisplayAspectScene) RamApp.getActiveScene();
                        history.put(currentAspect, currentAspect.getAspect());
        
                        if (history.containsValue(element)) {
                            currentAspect.setTransition(new SlideUpDownTransition(RamApp.getApplication(), 500, false));
                            RamApp.getApplication().loadScene(
                                    COREArtefactUtil.getReferencingExternalArtefact(element), element);
                            RamAbstractScene<?> scene = RamApp.getApplication().getExistingOrCreateModelScene(element);
                            if (scene instanceof DisplayAspectScene) {
                                ((DisplayAspectScene) scene).repushSections();
                            } else if (scene instanceof DisplayClassDiagramScene) {
                                ((DisplayClassDiagramScene) scene).repushSections();
                            }
                            RamApp.getApplication().getExistingOrCreateModelScene(element).getCanvas().addChild(nav);
                        } else {
                            currentAspect.setTransition(new SlideUpDownTransition(RamApp.getApplication(), 500, false));       
                            RamApp.getApplication().loadScene(
                                    COREArtefactUtil.getReferencingExternalArtefact(element), element);
                        }
                    }
        
                }, null);

    }

    @Override
    public void destroy() {
        // TODO: Really?
        RamApp.getApplication().getCanvas().addChild(this);
    }

    /**
     * Namer used by the Structural View menu below a Realization Model to handle the classes' Operations.
     * 
     * @return the Classifier Namer
     */
    public NavigatonBarNamer<Classifier> getNamerClassifier() {
        NavigatonBarNamer<Classifier> namerClassifier = new NavigationBarMenu.NavigatonBarNamer<Classifier>() {
            @Override
            public void initMenu(NavigationBarMenu menu, Classifier element) {
                List<Operation> operations = new ArrayList<>();
                Aspect aspect = (Aspect) element.eContainer().eContainer();
                for (Operation operation : element.getOperations()) {
                    MessageView messageView = MessageViewUtil.getMessageViewFor(aspect, operation);
                    
                    if (messageView != null) {
                        operations.add(operation);                        
                    }
                }
                
                toggleClassSelection(element);
                realizeOperationsMenu(menu, operations);
            }
        };
        return namerClassifier;
    }

    /**
     * Method called by the Classifier Namer to populate the menu with the operations.
     * 
     * @param menu where to add the operations found
     * @param operations the list of operations found
     */
    private void realizeOperationsMenu(NavigationBarMenu menu, List<Operation> operations) {
        menu.addMenuElement(operations, new AbstractDefaultListListener<Operation>() {
            @Override
            public void elementSelected(RamListComponent<Operation> list, Operation operation) {
                selected = null;
                boolean isPartial = operation.getPartiality() != RAMPartialityType.NONE;
                boolean isAbstract = operation.isAbstract();
                if (!isPartial && !isAbstract) {
                    Aspect aspect = (Aspect) operation.eContainer().eContainer().eContainer();
                    RamApp.getActiveAspectScene().showMessageView(MessageViewUtil.getMessageViewFor(aspect, operation));
                } else {
                    RamApp.getActiveScene().displayPopup(Strings.POPUP_PARTIAL_ABSTRACT_NO_BEHAVIOR);
                }
            }

        }, null);
    }

    /**
     * Namer used by the Display Aspect Scene to push an Abstract Message View section in the navbar.
     * 
     * @param aspect Aspect of the actual Display Aspect Scene we are in
     * @return the namer itself
     */
    public static NavigatonBarNamer<AspectMessageView> getAspectMessage(final Aspect aspect) {
        NavigatonBarNamer<AspectMessageView> namerAbstractMessageView =
            new NavigationBarMenu.NavigatonBarNamer<AspectMessageView>() {
                @Override
                public void initMenu(NavigationBarMenu menu, final AspectMessageView elementO) {
                    List<AspectMessageView> messageViews =
                            MessageViewUtil.getMessageViewsOfType(aspect, AspectMessageView.class);
                    
                    menu.addMenuElement(messageViews, new AbstractDefaultListListener<AspectMessageView>() {
                        @Override
                        public void elementSelected(RamListComponent<AspectMessageView> list,
                                AspectMessageView element) {
                            RamApp.getActiveAspectScene().switchToPreviousView();
                            RamApp.getActiveAspectScene().showMessageView(element);
                        }
                    }, new Filter<AspectMessageView>() {
                        @Override
                        public boolean shouldAddElement(AspectMessageView element) {
                            return !element.equals(elementO);
                        }
                    });
                }
            };
        
        return namerAbstractMessageView;
    }

    /**
     * Reference message namer.
     * NOT used (delete?)
     * 
     * @return namer itself
     */
    public static NavigatonBarNamer<MessageViewReference> getReferenceMessage() {
        NavigatonBarNamer<MessageViewReference> namerReferenceMessageView =
            new NavigationBarMenu.NavigatonBarNamer<MessageViewReference>() {

                @Override
                public void initMenu(NavigationBarMenu menu, MessageViewReference element) {
                    // menu.addMenuElement(element.getReferences().getSpecifies(), namer5, null);
                }
            };
        return namerReferenceMessageView;
    }

    /**
     * Namer used by the Display Aspect Scene Message View to handle the elaboration of a message View section.
     * 
     * @return the namer itself
     */
    public static NavigatonBarNamer<Operation> getMessage() {
        NavigatonBarNamer<Operation> namerMessageView = new NavigationBarMenu.NavigatonBarNamer<Operation>() {

            @Override
            public void initMenu(NavigationBarMenu menu, final Operation element) {
                List<EObject> d = EMFModelUtil.findCrossReferences(element, element.eContainingFeature());
                menu.addMenuElement(d, new AbstractDefaultListListener<EObject>() {
                    @Override
                    public void elementSelected(RamListComponent<EObject> list,
                            EObject element) {
                    }

                }, null);
            }
        };
        return namerMessageView;
    }

    /**
     * Method to toggle the selection of a class when needs to be highlighted or de-selected.
     * 
     * @param classifier the classifier that potentially will be highlighted
     */
    public void toggleClassSelection(Classifier classifier) {
        if (selected == null) {
            selected = classifier;
            ClassifierView<?> v = RamApp.getActiveAspectScene().getStructuralDiagramView().getClassViewOf(selected);
            v.setFillColor(Colors.FEATURE_ASSIGNEMENT_FILL_COLOR);
        } else {
            ClassifierView<?> w = RamApp.getActiveAspectScene().getStructuralDiagramView().getClassViewOf(selected);
            w.setFillColor(Colors.DEFAULT_ELEMENT_FILL_COLOR);
            selected = classifier;
            ClassifierView<?> v = RamApp.getActiveAspectScene().getStructuralDiagramView().getClassViewOf(selected);
            v.setFillColor(Colors.FEATURE_ASSIGNEMENT_FILL_COLOR);
        }
    }

    /**
     * Pushes a special section in the bar used for the Build Application mode.
     * 
     * @param icon the icon for the section
     * @param label the name of the section
     * @param namer the content
     * @param eObject the type of content
     */
    public void pushSectionBuildRM(PImage icon, String label, NavigatonBarNamerRM<Aspect> namer,
            Aspect eObject) {
        pushSectionBuildingRM(icon, label, namer, eObject);
    }

    /**
     * Pushes a special RM section in the bar NOT belonging to any Feature.
     * 
     * @param icon the icon for the section
     * @param label the name of the section
     * @param namer the content
     * @param eObject the type of content
     */
    public void pushSectionRMWithNoFeature(PImage icon, String label, NavigatonBarNamerRM<Aspect> namer,
            Aspect eObject) {
        pushRMWithNoFeature(icon, label, namer, eObject);
    }

    /**
     * Connecting method to create the NavigationBarSection for the building mode.
     * 
     * @param icon the icon for the section
     * @param label the name of the section
     * @param namer the content
     * @param eObject the type of content
     * @param <T> actual type
     */
    public <T> void pushSectionBuildingRM(PImage icon, String label, NavigatonBarNamerRM<T> namer, T eObject) {
        NavigationBarSection section = new NavigationBarSection(icon, label, namer, eObject, true);
        for (NavigationBarSection s : sections) {
            s.addExpandableButtonRM(null, null);
        }

        sections.push(section);
        // section.setNoStroke(false);
        topContainer.addChild(section);
        closeMenu();
    }

    /**
     * Peculiar RM Section that does not belong to any Feature.
     * 
     * @param icon the icon for the section
     * @param label the name of the section
     * @param namer the content
     * @param eObject the type of content
     * @param <T> actual type
     */
    public <T> void pushRMWithNoFeature(PImage icon, String label, NavigatonBarNamerRM<T> namer, T eObject) {
        NavigationBarSection section = new NavigationBarSection(icon, label, namer, eObject, false);
        sections.push(section);

        acrossNotationContainerF = new RamRectangleComponent();
        acrossNotationContainerF.setLayout(new VerticalLayout());

        RamImageComponent acrossModelNotation = new RamImageComponent(Icons.ICON_COLON_ACROSS_MODEL, MTColor.WHITE);
        acrossModelNotation.setSizeLocal(8, 24);
        acrossModelNotation.setAutoMaximizes(false);
        acrossModelNotation.setAutoMinimizes(false);
        acrossModelNotation.setFillColor(MTColor.RED);
        acrossModelNotation.setPickable(false);
        acrossNotationContainerF.addChild(acrossModelNotation);
        topContainer.addChild(acrossNotationContainerF);
        topContainer.addChild(section);
        closeMenu();

    }

    /**
     * Triggered when a Concern Reuse is being queried.
     *
     * @param aspect Aspect to be collapsed
     */
    public void collapseStartingEnvironment(Aspect aspect) {
        RamImageComponent expandPreviousConcern = new RamImageComponent(Icons.ICON_REUSE_COLLAPSE, MTColor.WHITE);
        expandPreviousConcern.setMinimumSize(80, 80);
        expandPreviousConcern.setMaximumSize(80, 80);
        expandPreviousConcern.setBufferSize(Cardinal.WEST, 10);
        expandPreviousConcern.setBufferSize(Cardinal.EAST, 10);

        returnToConcernButton = new RamButton(expandPreviousConcern);

        COREFeature feature = COREArtefactUtil.getReferencingExternalArtefact(aspect).getScene().getRealizes().get(0);

        final COREConcern concern = (COREConcern) feature.eContainer().eContainer();
        currentConcern = concern;
        topContainer.removeAllChildren();
        topContainer.addChild(returnToConcernButton);

        returnToConcernButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                DisplayAspectScene reusedAspect = (DisplayAspectScene) RamApp.getActiveScene();
                if (reusedAspect.getCurrentView() instanceof MessageViewView) {
                    reusedAspect.switchToPreviousView();
                }

                topContainer.removeAllChildren();
                handleSections();

                pushSection(concern.getName(), getCoreNamer(), concern);
                DisplayAspectSceneHandler d =
                        (DisplayAspectSceneHandler) RamApp.getActiveScene().getHandler();
                d.switchBackTo((DisplayAspectScene) RamApp.getActiveScene());
            }
        });
    }

    /**
     * Method used by the closeAspectScene(lowerLevelAspectScene) when it comes to going back from a split view.
     * It restores the navigation bar with the information of the UPPER aspect
     */
    public void returnNormalViewFromSplit() {
        DisplayAspectScene reusedAspect = (DisplayAspectScene) RamApp.getActiveScene();
        if (reusedAspect.getCurrentView() instanceof MessageViewView) {
            reusedAspect.switchToPreviousView();
        }

        topContainer.removeAllChildren();
        handleSections();
        pushSection(currentConcern.getName(), getCoreNamer(), currentConcern);
        reusedAspect.repushSections();
    }

    /**
     * Method used to clear all the sections of the bar when it comes to handling RM with no Feature.
     * This will be also called when user wants to check out a reused design model.
     */
    public void handleSections() {
        sections.clear();
    }

    /**
     * Retrieves the size of the navigation bar.
     * Used to understand whether we're in a weaving mode inside a full concern OR we're actually
     * in a build Application mode where the navigation bar is empty and must be populated.
     * 
     * @return the size of navbar
     */
    public int getSectionSize() {
        return sections.size();
    }

    /**
     * Used to pop the last element of history.
     * 
     */
    public static void popHistory() {
        if (!histories.isEmpty()) {
            histories.pop();
        }
    }

    /**
     * Used to go back to the desired environment chosen from the History menu.
     * 
     * @param scene Scene to return to.
     */
    public static void returnToSection(RamAbstractScene<?> scene) {
        RamApp.getApplication().removeScene(scene);
        
        // you want to return to an aspect scene
        if (scene instanceof DisplayAspectScene) {
            DisplayAspectScene sceneIwannaGoTo = (DisplayAspectScene) scene;
            RamApp.getActiveScene().setTransition(new SlideTransition(RamApp.getApplication(), 700, false));
            RamApp.getApplication().loadScene(
                    COREArtefactUtil.getReferencingExternalArtefact(sceneIwannaGoTo.getAspect()),
                    sceneIwannaGoTo.getAspect());
            
        }
    }

    /**
     * Modality used to temporarily hide the bar when it comes to an occurrence of DisplayConcernSelectScene comes up.
     * The bar will be hidden so that the user can focus on the Feature Model of the Concern that wants to be reused.
     */
    public void concernSelectMode() {
        if (topContainer.isVisible()) {
            topContainer.setVisible(false);
        } else {
            topContainer.setVisible(true);
        }
    }

    /**
     * Method called by ConcernEditScene handler to check if the Feature Model structure has been modified.
     * If it needs to be saved a popup confirm will be displayed and then this method will be called.
     */
    public void wipeNavigationBar() {
        selected = null;
        history.clear();
        histories.clear();
        sections.clear();
        topContainer.removeAllChildren();
    }

    /**
     * Returns the Concern on which the user is working on.
     * 
     * @return the COREConcern the user is working inside of
     */
    public COREConcern getCurrentConcern() {
        return currentConcern;
    }

    /**
     * Sets the current concern.
     * 
     * @param concern the current concern
     */
    public void setCurrentConcern(COREConcern concern) {
        currentConcern = concern;
    }

    /**
     * Returns the perspective that the user is working in.
     * 
     * @return the COREPerspective the user is working inside of
     */
    public COREPerspective getCurrentPerspective() {
        return currentPerspective;
    }

    /**
     * Sets the current perspective.
     * 
     * @param perspective the current perspective
     */
    public void setCurrentPerspective(COREPerspective perspective) {
        currentPerspective = perspective;
    }

    /**
     * Enables the specialized navigation bar after a model has been woven.
     * 
     * @param weavingAspect the aspect for which to enable the weaving result mode for
     */
    public void weavingInterfaceMode(Aspect weavingAspect) {
        containsWeaveCrumb = true;

        RamImageComponent expandPreviousConcern = new RamImageComponent(Icons.ICON_WEAVE_COLLAPSE, MTColor.WHITE);
        expandPreviousConcern.setMinimumSize(80, 80);
        expandPreviousConcern.setMaximumSize(80, 80);
        expandPreviousConcern.setBufferSize(Cardinal.WEST, 10);
        expandPreviousConcern.setBufferSize(Cardinal.EAST, 10);

        returnToConcernButton = new RamButton(expandPreviousConcern);
        
        COREArtefact externalArtefact = COREArtefactUtil.getReferencingExternalArtefact(weavingAspect);
        
//        COREFeature feature = externalArtefact.getScene().getRealizes().get(0);
//
//        final COREConcern concern = (COREConcern) feature.eContainer().eContainer();
        // COREFeature feature = externalArtefact.getScene().getRealizes().get(0);

        final COREConcern concern = (COREConcern) externalArtefact.getCoreConcern() == null
                ? externalArtefact.getTemporaryConcern() : (COREConcern) externalArtefact.getCoreConcern();

        currentConcern = concern;
        topContainer.removeAllChildren();
        topContainer.addChild(returnToConcernButton);

        returnToConcernButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                DisplayAspectScene reusedAspectScene = (DisplayAspectScene) RamApp.getActiveAspectScene();
                DisplayAspectSceneHandler handler = (DisplayAspectSceneHandler) reusedAspectScene.getHandler();
                
                if (reusedAspectScene.getCurrentView() instanceof MessageViewView) {
                    reusedAspectScene.switchToPreviousView();
                }

                DisplayAspectScene previousScene = (DisplayAspectScene) reusedAspectScene.getPreviousScene();
                if (previousScene.getAspect().getName().contains(WOVEN_DELIMITER)) {
                    topContainer.removeAllChildren();
                    handleSections();

                    pushSection(currentConcern.getName(), getCoreNamer(), currentConcern);
                    handler.switchBackTo(reusedAspectScene);

                    weavingInterfaceMode(reusedAspectScene.getAspect());
                    previousScene.repushSections();
                } else {
                    topContainer.removeAllChildren();
                    handleSections();
                    containsWeaveCrumb = false;
                    pushSection(concern.getName(), getCoreNamer(), concern);

                    handler.switchBackTo(reusedAspectScene);
                }
            }
        });
    }

    /**
     * Returns whether the navigation bar contains a weave crumb.
     * 
     * @return true, if it contains a weave crumb, false otherwise
     */
    public boolean containsWeaveCrumb() {
        return containsWeaveCrumb;
    }

}
