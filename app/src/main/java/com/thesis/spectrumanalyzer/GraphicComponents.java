package com.thesis.spectrumanalyzer;

import android.view.View;
import android.widget.Button;

/**
 * Parsing components and id view
 */
class GraphicComponents {
    private MainActivity activity;
    private GraphView graph;

    GraphicComponents(MainActivity activity) { this.activity = activity; }

    void setGraphicComponents(){
        setupGraph();
        setupButtons();
    }

    private void setupGraph() {
        graph = activity.findViewById(R.id.graphView);
    }

    private void setupButtons(){
        final Button aWeighting = activity.findViewById(R.id.aWeighting);
        final Button cWeighting = activity.findViewById(R.id.cWeighting);

        aWeighting.setActivated(true);
        aWeighting.setEnabled(false);
        aWeighting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                activity.getHandler().changeState(FormulasUtil.State.A_WEIGHTING);
                aWeighting.setEnabled(false);
                cWeighting.setEnabled(true);
            }
        });

        cWeighting.setActivated(false);
        cWeighting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                activity.getHandler().changeState(FormulasUtil.State.C_WEIGHTING);
                aWeighting.setEnabled(true);
                cWeighting.setEnabled(false);
            }
        });
    }

    GraphView getGraph() { return graph; }
}
