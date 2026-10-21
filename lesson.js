const answerKey = {
  q1: {
    answer: "b",
    correct: "Good call. Background access collects location when the feature may not need it. Use the narrowest permission that still supports the task, and review the privacy notice.",
    incorrect: "Not quite. GPS can record movement over time, and a free app can still share data. Ask whether background access is needed and choose the narrowest useful permission."
  },
  q2: {
    answer: "a",
    correct: "Right. Repeated points can reveal a home, school or workplace, routes, and routines. A location history can say more than a single destination.",
    incorrect: "Try again. Location points do not directly reveal passwords or text typed in other apps, but repeated points can expose places visited and routines."
  },
  q3: {
    answer: "b",
    correct: "Correct, with important limits: COPPA is a U.S. federal rule about covered services collecting personal information from children under 13. It is not a rule for every user, and exceptions can apply.",
    incorrect: "Review the scope: COPPA is a U.S. regulation, not just advice, but it does not cover every app user. For covered services, notice and verifiable parental consent generally apply, subject to exceptions."
  }
};

const questionCards = document.querySelectorAll(".quiz-card");

function updateScore() {
  let answered = 0;
  let correct = 0;

  questionCards.forEach((card) => {
    const question = card.dataset.question;
    const selection = card.querySelector(`input[name="${question}"]:checked`);
    if (selection) {
      answered += 1;
      if (selection.value === answerKey[question].answer) correct += 1;
    }
  });

  const score = document.querySelector("#score");
  score.textContent = answered === questionCards.length
    ? `Progress: ${correct} of ${questionCards.length} decisions correct. Read each explanation and revise your thinking if needed.`
    : `You have checked ${answered} of ${questionCards.length} decisions.`;
}

questionCards.forEach((card) => {
  const question = card.dataset.question;
  const feedback = card.querySelector(".feedback");

  card.querySelector(".check-button").addEventListener("click", () => {
    const selection = card.querySelector(`input[name="${question}"]:checked`);
    if (!selection) {
      feedback.textContent = "Choose an answer first, then check your reasoning.";
      feedback.className = "feedback feedback-prompt";
      return;
    }

    const isCorrect = selection.value === answerKey[question].answer;
    feedback.textContent = isCorrect ? answerKey[question].correct : answerKey[question].incorrect;
    feedback.className = isCorrect ? "feedback feedback-correct" : "feedback feedback-retry";
    updateScore();
  });
});

const permission = document.querySelector("#permission");
const permissionResult = document.querySelector("#permission-result");

function updatePermissionResult() {
  const messages = {
    always: "The app could access location in the background. That may be useful for some features, but it collects more than turn-by-turn directions usually need.",
    "while-using": "The app can use location while you actively use it. This is a narrower choice for navigation, though the app's own collection and sharing practices still matter.",
    never: "The app cannot use your location. You can still enter a destination manually, but live directions may not work."
  };

  permissionResult.textContent = messages[permission.value];
}

permission.addEventListener("change", updatePermissionResult);
updatePermissionResult();
