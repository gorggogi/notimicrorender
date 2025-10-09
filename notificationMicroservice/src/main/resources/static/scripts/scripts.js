document.addEventListener('DOMContentLoaded', function () {
    console.log("Admin scripts loaded.");

    // --- Logic for the confirmation modal on announcement-form.html ---
    const announcementForm = document.getElementById('announcementForm');
    
    // Check if we are on the page with the announcement form
    if (announcementForm) {
        const modal = document.getElementById('confirmationModal');
        const confirmButton = document.getElementById('confirmButton');
        const cancelButton = document.getElementById('cancelButton');
        const scheduleInput = document.getElementById('scheduleTime');
        const scheduleWarning = document.getElementById('scheduleWarning');

        // Show/hide the schedule warning message
        if (scheduleInput) {
            scheduleInput.addEventListener('input', function() {
                if (scheduleInput.value) {
                    scheduleWarning.classList.remove('hidden');
                } else {
                    scheduleWarning.classList.add('hidden');
                }
            });
        }
        
        // Intercept form submission to show the modal
        announcementForm.addEventListener('submit', function (e) {
            e.preventDefault(); // Stop form from submitting immediately
            
            const alertSelect = document.getElementById('alertId');
            const alertType = alertSelect.options[alertSelect.selectedIndex].text;
            const message = document.getElementById('message').value;
            const scheduleTime = scheduleInput.value;

            // Populate modal with form data
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
            
            // Show the modal
            modal.classList.remove('hidden');
        });
        
        // Handle confirm button click
        if (confirmButton) {
            confirmButton.addEventListener('click', function() {
                announcementForm.submit(); // Submit the form
            });
        }

        // Handle cancel actions
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
                // Hide modal if the background is clicked
                if (e.target === modal) {
                    hideModal();
                }
            });
        }
    }
    // --- End of announcement-form.html logic ---
});