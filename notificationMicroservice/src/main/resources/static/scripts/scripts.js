document.addEventListener('DOMContentLoaded', function () {
    console.log("Admin scripts loaded.");

    // --- Logic to disable all form buttons on submit to prevent multiple requests ---
    const allForms = document.querySelectorAll('form');
    allForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            // Find all submit buttons within the submitted form that are not part of a modal
            const submitButtons = form.querySelectorAll('button[type="submit"]:not(.modal-button)');
            
            submitButtons.forEach(button => {
                button.disabled = true;
                button.textContent = 'Processing...';
                button.classList.add('opacity-50', 'cursor-not-allowed');
            });
        });
    });

    // --- Logic for the confirmation modal on announcement-form.html ---
    const announcementForm = document.getElementById('announcementForm');
    
    if (announcementForm) {
        const modal = document.getElementById('confirmationModal');
        const confirmButton = document.getElementById('confirmButton');
        const cancelButton = document.getElementById('cancelButton');
        const scheduleInput = document.getElementById('scheduleTime');
        const scheduleWarning = document.getElementById('scheduleWarning');

        if (scheduleInput) {
            scheduleInput.addEventListener('input', function() {
                scheduleWarning.classList.toggle('hidden', !scheduleInput.value);
            });
        }
        
        announcementForm.addEventListener('submit', function (e) {
            e.preventDefault();
            
            const alertSelect = document.getElementById('alertId');
            const alertType = alertSelect.options[alertSelect.selectedIndex].text;
            const message = document.getElementById('message').value;
            const scheduleTime = scheduleInput.value;

            document.getElementById('confirmType').textContent = alertType;
            document.getElementById('confirmMessage').textContent = message;

            const confirmScheduleLi = document.getElementById('confirmScheduleLi');
            if (scheduleTime) {
                const formattedDate = new Date(scheduleTime).toLocaleString('en-US', { dateStyle: 'medium', timeStyle: 'short' });
                document.getElementById('confirmSchedule').textContent = formattedDate;
                confirmScheduleLi.classList.remove('hidden');
            } else {
                confirmScheduleLi.classList.add('hidden');
            }
            
            modal.classList.remove('hidden');
        });
        
        if (confirmButton) {
            confirmButton.addEventListener('click', function() {
                // Manually disable the modal button before submitting the form
                confirmButton.disabled = true;
                confirmButton.textContent = 'Sending...';
                confirmButton.classList.add('opacity-50', 'cursor-not-allowed');
                
                announcementForm.submit();
            });
        }

        const hideModal = () => {
            if (modal) {
                modal.classList.add('hidden');
            }
        };

        if (cancelButton) {
            cancelButton.addEventListener('click', hideModal);
        }

        if (modal) {
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    hideModal();
                }
            });
        }
    }
});
