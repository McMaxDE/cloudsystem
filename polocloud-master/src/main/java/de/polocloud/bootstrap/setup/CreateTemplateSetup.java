package de.polocloud.bootstrap.setup;

import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.template.SimpleTemplate;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.setup.Setup;
import de.polocloud.setup.SetupBuilder;
import de.polocloud.setup.Step;
import de.polocloud.setup.accepter.StepAcceptor;
import de.polocloud.setup.accepter.StepAnswer;

import java.util.Arrays;
import java.util.List;

public class CreateTemplateSetup extends StepAcceptor implements Setup {

    private ITemplateService templateService;

    public CreateTemplateSetup(ITemplateService templateService) {
        this.templateService = templateService;
    }

    @Override
    public void sendSetup() {
        SetupBuilder setupBuilder = new SetupBuilder(this);
        Step step = setupBuilder.createStep("What is the name of the new template?");


        //TODO get all possible Wrapper names
        step.addStep("What is the minimum amount of services?", isInteger())
            .addStep("What is the maximal amount of services?", isInteger())
            .addStep("What is the amount of max players?", isInteger())
            .addStep("What is the maximal memory of this service", isInteger())
            .addStep("What is the template type of this service?", TemplateType.MINECRAFT.getDisplayName(), TemplateType.PROXY.getDisplayName())
            .addStep("What is the game verison?", GameServerVersion.PROXY.getTitle(), GameServerVersion.SPIGOT_1_8_8.getTitle())
            .addStep("What is name of the Wrapper(s) ?");

        setupBuilder.setStepAnswer(new StepAnswer() {
            @Override
            public void callFinishSetup(List<Step> steps) {
                String name = steps.get(0).getAnswer();
                int maxServerCount = Integer.parseInt(steps.get(2).getAnswer());
                int minServerCount = Integer.parseInt(steps.get(1).getAnswer());
                int maxPlayers = Integer.parseInt(steps.get(3).getAnswer());
                int memory= Integer.parseInt(steps.get(4).getAnswer());

                TemplateType templateType = TemplateType.valueOf(steps.get(5).getAnswer().toUpperCase());
                GameServerVersion gameServerVersion = Arrays.stream(GameServerVersion.values()).filter(key -> key.getTitle().equalsIgnoreCase(steps.get(6).getAnswer())).findAny().get();
                String[] wrappers =  steps.get(7).getAnswer().replaceAll(" ", "").split(",");

                ITemplate template = new SimpleTemplate(name, maxServerCount, minServerCount, templateType, gameServerVersion, maxPlayers, memory, wrappers);
                templateService.getTemplateSaver().save(template);
                Logger.log(LoggerType.INFO, ConsoleColors.LIGHT_BLUE.getAnsiCode() + "PoloCloud " + ConsoleColors.GRAY.getAnsiCode() + "» You " +
                    ConsoleColors.GREEN.getAnsiCode() + "complete " + ConsoleColors.GRAY.getAnsiCode() + "the setup.");
            }
        });
        setupBuilder.nextQuestion(step, Logger.getConsoleReader());
    }

    @Override
    public void cancelSetup() {

    }
}