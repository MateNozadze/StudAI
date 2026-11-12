
from typing import TypedDict, List, Dict, Any, Optional


class StudyState(TypedDict):

    """
    The global state of the Study Agent, which carries
    data between the nodes of the graph.
    """

    material: str
    subject: Optional[str]
    questionCount: int
    difficulty: str
    questions: List[Dict[str, Any]]