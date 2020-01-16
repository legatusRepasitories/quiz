let currentQuestionIndex = 0;
let currentQuestion;
let questionList = [];
let variantInput = null;
let myHeaders = new Headers();
let token = document.querySelector("meta[name='_csrf']").content;
let header = document.querySelector("meta[name='_csrf_header']").content;
myHeaders.append(header, token);
myHeaders.append('Content-Type', 'application/json');

$(document).ready(function () {
    $("#flip").click(function () {
        $("#panel").slideDown("slow", function () {
            $("#flip").remove();
            startQuiz();
        });
    });
});

async function startQuiz() {
    questionList = await fetch('/api/question/all')
        .then(response => response.json());
    await getStandardVariantInput();

    showNextQuestion();
}

let questionText = document.getElementById('questionText');
let variants = document.getElementById('variants');

function showNextQuestion() {
    clearPage();

    if (currentQuestionIndex >= questionList.length) {
        document.getElementById("quizForm").hidden = true;
        document.getElementById("noQuestions").innerText = "No more questions! Total amount of answered questions: " + currentQuestionIndex;
        return;
    }
    currentQuestion = questionList[currentQuestionIndex];
    currentQuestion.variants = shuffle(currentQuestion.variants);
    questionText.innerText = currentQuestion.text;



    for (let v of currentQuestion.variants) {
        variants.insertAdjacentHTML('beforeend', variantInput);
        variants.lastElementChild.querySelector('p').innerText = v;

    }

}

async function getStandardVariantInput() {
    variantInput = await fetch('/quizElement.html').then(response => response.text());
}

async function validate() {

    let rightVariants = [];
    let wrongVariants = [];

    for (let v of variants.children) {
        let variantText = v.querySelector('p').innerText.trim();
        v.querySelector('input').checked ? rightVariants.push(variantText) : wrongVariants.push(variantText);
    }

    let question = {
        id: currentQuestion.id,
        questionText: currentQuestion.text,
        rightVariants: rightVariants,
        wrongVariants: wrongVariants
    };

    let isCorrect = await fetch(`/api/question/validate`, {
        method: 'POST',
        headers: myHeaders,
        body: JSON.stringify(question),
    }).then(response => response.json());


    if (isCorrect) {
        currentQuestionIndex++;
        showNextQuestion();
    } else {
        document.getElementById("quizForm").hidden = true;
        document.getElementById("end").innerText = "Wrong! Total amount of answered questions: " + currentQuestionIndex;
    }

}

function shuffle(a) {
    for (let i = a.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [a[i], a[j]] = [a[j], a[i]];
    }
    return a;
}

function clearPage() {
    while (variants.firstChild) {
        variants.removeChild(variants.firstChild);
    }
}