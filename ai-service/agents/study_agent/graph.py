from langgraph.graph import StateGraph, START, END
from agents.study_agent.nodes import analyze_material_node, generate_quiz_node
from agents.study_agent.state import StudyState


class StudyGraph:
    """
    Represents the study graph for the StudyAgent.
    This class manages the workflow of nodes using LangGraph.
    """
    
    def __init__(self):
        self.app = self._build_graph()

    def _build_graph(self):
        workflow = StateGraph(StudyState)
        
        workflow.add_node("analyze_material", analyze_material_node)
        workflow.add_node("generate_quiz", generate_quiz_node)
        
        workflow.add_edge(START, "analyze_material")
        workflow.add_edge("analyze_material", "generate_quiz")
        workflow.add_edge("generate_quiz", END)
        
        return workflow.compile()

    def run(self, initial_state: StudyState) -> dict:

        """
        Executes the study graph workflow starting from the initial state.
        """
        
        return self.app.invoke(initial_state)

study_graph = StudyGraph()