package com.qmaker.converter.core;

import com.qmaker.core.engines.IOHandler;
import com.qmaker.core.engines.QSystem;
import com.qmaker.core.entities.QSummary;
import com.qmaker.core.entities.Qcm;
import com.qmaker.core.interfaces.MarksPolicyDefinition;
import com.qmaker.core.io.IOInterface;
import com.qmaker.core.io.QFile;
import com.qmaker.core.io.QPackage;
import com.qmaker.core.utils.MemoryIoInterface;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import istat.android.base.tools.ToolKits;


public class TopGradeWebsiteParser {
    QSystem system;

    private TopGradeWebsiteParser() {
        system = new QSystem(MemoryIoInterface.getDefault());
    }

    public static QPackage parse(String uri) throws IOException {
        TopGradeWebsiteParser parser = new TopGradeWebsiteParser();
        URL url = new URL(uri);
        QPackage.Builder builder = parser.createQpackageBuilder(url.openStream());
        builder.setUri(uri);
        return builder.create();
    }

    private QPackage.Builder createQpackageBuilder(InputStream inputStream) {
        String contentString = ToolKits.Stream.streamToString(inputStream);
        return createQpackageBuilder(contentString);
    }

    private QPackage.Builder createQpackageBuilder(String html) {
        QPackage.Builder builder = system.packageBuilder();
        Document doc = Jsoup.parse(html);
        String nextQuestionId;
        int index = 0;
        Element elementQuestionId;
        Element elementQuestionNumber;
        List<Qcm> qcmList = new ArrayList<>();
        int durationSecond = 0;
        Qcm qcm;
        while ((elementQuestionId = doc.getElementById("questionId" + index)) != null) {
            elementQuestionNumber = doc.getElementById("questionNumber" + index);
            qcm = new Qcm();
            nextQuestionId = elementQuestionId.text();
            fillQcmMataData(qcm, doc, nextQuestionId);
            qcm.putExtra(Qcm.EXTRA_ALLOWED_TIME, doc.getElementById("questionTime" + index).text());
            qcm.setId(com.qmaker.core.utils.ToolKits.generateID(null, nextQuestionId));
            qcm.setQuestion(createQuestion(elementQuestionNumber, nextQuestionId));
            qcm.setPropositions(createPropositionList(elementQuestionNumber, nextQuestionId));
            qcmList.add(qcm);
            System.out.println(qcm.toString());
            durationSecond += qcm.getExtras().getInt(Qcm.EXTRA_ALLOWED_TIME, 0);
            index++;
        }
        QSummary summary = new QSummary(doc.title());
        summary.setDuration(durationSecond);
        summary.getConfig().setMarksPolicyDefinition(MarksPolicyDefinition.ONE_PER_SUCCESS);
        summary.getConfig().setRandomEnable(true);
        fillSummaryMetaData(doc);
        builder.setQSummary(summary);
        builder.setQcms(qcmList);
        builder.setType(QFile.TYPE_ARCHIVE);
        return builder;
    }

    private void fillSummaryMetaData(Document doc) {

    }


    private List<Qcm.Proposition> createPropositionList(Element doc, String nextQuestionId) {
        Elements elements = doc.getElementsByTag("button");
        List<Qcm.Proposition> propositions = new ArrayList<>();
        Qcm.Proposition proposition;
        for (Element element : elements) {
            proposition = createPropositionFromButtonElement(element);
            if (proposition != null) {
                propositions.add(proposition);
            }
        }
        return propositions;
    }

    private Qcm.Proposition createPropositionFromButtonElement(Element element) {
        Elements pElements = element.getElementsByTag("p");
        if (pElements != null && !pElements.isEmpty()) {
            Qcm.Proposition proposition = new Qcm.Proposition();
            proposition.setLabel(pElements.get(0).text());
            proposition.setTruth(element.id() != null && element.id().endsWith("1"));
            return proposition;
        }
        return null;
    }

    private void fillQcmMataData(Qcm qcm, Element element, String questionId) {

    }

    private void fillpropositionMataData(Qcm qcm, Element element, String questionId) {

    }

    private Qcm.Question createQuestion(Element doc, String nextQuestionId) {
        Qcm.Question question = new Qcm.Question();
        question.setLabel(doc.getElementById("question" + nextQuestionId).text());
        return question;
    }
}
