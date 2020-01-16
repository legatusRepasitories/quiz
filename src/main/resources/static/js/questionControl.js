let questionId = document.getElementById('questionId').value;

if (questionId === '0') {
    document.querySelector('form').firstElementChild.hidden = true;
}

let token = document.querySelector("meta[name='_csrf']").content;
let header = document.querySelector("meta[name='_csrf_header']").content;
let myHeaders = new Headers();
myHeaders.append(header, token);

let variants = document.getElementById('variants');
let variantInput;

function validateForms() {
    let form = document.querySelector('form');
    let isFormValid = form.checkValidity();
    form.classList.add('was-validated');
    let isCheckBoxesValid = validateCheckBoxes();
    return isFormValid && isCheckBoxesValid;
}

function validateCheckBoxes() {
    let right_count = 0;
    let wrong_count = 0;
    let message = document.getElementById('invalid-checkbox-message');


    for (let v of variants.children) {


        v.querySelector('input').checked ? right_count++ : wrong_count++;
    }
    if (right_count === 0 || wrong_count === 0 ||
        right_count + wrong_count > 7 ||
        right_count + wrong_count < 3) {

        message.hidden = false;
        return false;
    } else {
        message.hidden = true;
        return true;
    }
}


async function submitQuestion() {

    if (!validateForms()) {
        return;
    }

    let rightVariants = [];
    let wrongVariants = [];

    for (let v of variants.children) {

        let variantText = v.querySelector('textarea').value.trim();

        v.querySelector('input').checked ? rightVariants.push(variantText) : wrongVariants.push(variantText);
    }

    let questionText = document.getElementById('questionText').value.trim();

    let question = {
        id: questionId,
        text: questionText,
        rightVariants: rightVariants,
        wrongVariants: wrongVariants
    };


    myHeaders.append('Content-Type', 'application/json');

    questionId === '0' ? await createQuestion(question) : await updateQuestion(question);

    toAllQuestions();
}

async function createQuestion(question) {


     await fetch(`/api/question`, {
        method: 'POST',
        headers: myHeaders,
        body: JSON.stringify(question),
    });

}

async function updateQuestion(question) {

    await fetch(`/api/question`, {
        method: 'PUT',
        headers: myHeaders,
        body: JSON.stringify(question)
    });
}

async function deleteQuestion() {

    await fetch(`/api/question/${questionId}`, {
        method: 'DELETE',
        headers: myHeaders,
    });
    toAllQuestions();
}

function toAllQuestions() {
    window.location.href = '/questions';
}

async function addVariant() {
    if (variantInput == null) {
        await getStandardVariantInput();
    }
    variants.insertAdjacentHTML('beforeend', variantInput);
}

function removeVariant() {
    variants.removeChild(variants.lastElementChild);
}

async function getStandardVariantInput() {
    variantInput = await fetch('/standardInput.html').then(response => response.text());
}